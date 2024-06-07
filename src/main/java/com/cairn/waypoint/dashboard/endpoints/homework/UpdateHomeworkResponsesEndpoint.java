package com.cairn.waypoint.dashboard.endpoints.homework;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.homework.dto.UpdateHomeworkResponseDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.homework.dto.UpdateHomeworkResponseDetailsListDto;
import com.cairn.waypoint.dashboard.entity.Homework;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.HomeworkResponse;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolStep;
import com.cairn.waypoint.dashboard.entity.enumeration.StepStatusEnum;
import com.cairn.waypoint.dashboard.service.data.HomeworkDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolStepDataService;
import com.cairn.waypoint.dashboard.utility.fileupload.S3FileUpload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@Tag(name = "Homework")
public class UpdateHomeworkResponsesEndpoint {

  public static final String PATH = "/api/homework/{homeworkId}";
  private final HomeworkDataService homeworkDataService;
  private final S3FileUpload s3FileUpload;
  private final ProtocolStepDataService protocolStepDataService;
  private final ProtocolDataService protocolDataService;

  @Value("${waypoint.dashboard.s3.homework-response-key-prefix}")
  private String homeworkResponseKeyPrefix;

  public UpdateHomeworkResponsesEndpoint(HomeworkDataService homeworkDataService,
      S3FileUpload s3FileUpload, ProtocolStepDataService protocolStepDataService,
      ProtocolDataService protocolDataService) {
    this.homeworkDataService = homeworkDataService;
    this.s3FileUpload = s3FileUpload;
    this.protocolStepDataService = protocolStepDataService;
    this.protocolDataService = protocolDataService;
  }

  @PatchMapping(value = PATH, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @PreAuthorize("hasAnyAuthority('SCOPE_homework.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Updates the homework question responses of the provided homework ID.",
      description = "Updates the homework question responses of the provided homework ID. Requires the `homework.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              description = "Updated - Homework update was successful"),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))}),})
  public ResponseEntity<?> updateHomeworkResponses(@PathVariable Long homeworkId,
      @RequestPart("json") @Parameter(schema = @Schema(type = "string", format = "binary")) UpdateHomeworkResponseDetailsListDto updateHomeworkResponseDetailsListDto,
      @RequestPart("files") Optional<MultipartFile[]> files, Principal principal) {
    Optional<Homework> homeworkToBeUpdated;

    Map<Long, UpdateHomeworkResponseDetailsDto> homeworkQuestions = updateHomeworkResponseDetailsListDto.getResponses()
        .stream()
        .collect(
            Collectors.toMap(UpdateHomeworkResponseDetailsDto::getQuestionId, Function.identity()));

    if ((homeworkToBeUpdated = homeworkDataService.getHomeworkById(homeworkId)).isEmpty()) {
      return generateFailureResponse("Homework with ID [" +
              homeworkId + "] does not exists",
          HttpStatus.NOT_FOUND);
    } else {
      if (!homeworkToBeUpdated.get().getHomeworkQuestions().stream()
          .map(HomeworkResponse::getHomeworkQuestion)
          .map(HomeworkQuestion::getId)
          .collect(Collectors.toSet()).containsAll(homeworkQuestions.keySet())) {
        return generateFailureResponse("Provided Question IDs are not associated with the homework",
            HttpStatus.UNPROCESSABLE_ENTITY);
      }

      Homework homeworkToSave = stageHomeworkResponses(homeworkQuestions, files.orElse(null),
          homeworkToBeUpdated.get(), principal.getName());

      Homework updatedHomework = homeworkDataService.saveHomework(homeworkToSave);

      ProtocolStep protocolStepToUpdate = updatedHomework.getAssociatedProtocolStep();

      if (updatedHomework.getHomeworkQuestions().stream().anyMatch(homeworkResponse ->
          homeworkResponse.getHomeworkQuestion().getRequired() &&
              (homeworkResponse.getResponse() == null || homeworkResponse.getResponse()
                  .isEmpty()))) {
        protocolStepToUpdate.setStatus(StepStatusEnum.IN_PROGRESS);
      } else {
        protocolStepToUpdate.setStatus(StepStatusEnum.DONE);
      }

      ProtocolStep updatedProtocolStep = protocolStepDataService.saveProtocolStep(
          protocolStepToUpdate);

      Protocol protocolToUpdate = updatedProtocolStep.getParentProtocol();
      if (protocolToUpdate.getProtocolSteps().stream()
          .allMatch(protocolStep -> protocolStep.getStatus().equals(StepStatusEnum.DONE))) {
        protocolToUpdate.setCompletionDate(LocalDate.now());
      } else {
        protocolToUpdate.setCompletionDate(null);
      }
      this.protocolDataService.updateProtocol(protocolToUpdate);

      return ResponseEntity.status(HttpStatus.OK)
          .body("Homework with ID [" + updatedHomework.getId() + "] and name ["
              + updatedHomework.getName() + "] updated successfully");
    }
  }

  private Homework stageHomeworkResponses(
      Map<Long, UpdateHomeworkResponseDetailsDto> homeworkQuestions,
      final MultipartFile[] files, Homework homework, String modifiedBy) {
    Function<String, Optional<MultipartFile>> fileProvider;
    if (files != null && files.length > 0 && files[0] != null
        && files[0].getOriginalFilename() != null && !files[0].getOriginalFilename()
        .isEmpty()) {
      fileProvider = (filename) -> Stream.of(files)
          .filter(file -> filename.equals(file.getOriginalFilename()))
          .findFirst();
    } else {
      fileProvider = (filename) -> Optional.empty();
    }

    homework.getHomeworkQuestions()
        .forEach(homeworkResponse -> {
          UpdateHomeworkResponseDetailsDto responseToUpdate = homeworkQuestions.get(
              homeworkResponse.getHomeworkQuestion().getId());

          Optional<MultipartFile> fileToUpload;
          String uploadedFileName;
          try {
            if ((fileToUpload = fileProvider.apply(
                responseToUpdate.getUserResponse())).isPresent()) {
              try {
                uploadedFileName = s3FileUpload.uploadFile(fileToUpload.get(), modifiedBy,
                    homeworkResponseKeyPrefix);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
              responseToUpdate.setUserResponse(uploadedFileName);
              responseToUpdate.setUploadedFile(fileToUpload.get());
            }

            homeworkResponse.setModifiedBy(modifiedBy);
            homeworkResponse.setResponse(responseToUpdate.getUserResponse());
            homeworkResponse.setFileGuid(
                fileToUpload.isPresent() ? UUID.randomUUID().toString() : null);
          } catch (NullPointerException e) {
            //Nothing to be done
          }
        });

    return homework;
  }

  private ResponseEntity<ErrorMessage> generateFailureResponse(String message, HttpStatus status) {
    log.warn(message);
    return new ResponseEntity<>(
        ErrorMessage.builder()
            .path(PATH)
            .timestamp(LocalDateTime.now())
            .status(status.value())
            .error(message)
            .build(),
        status
    );
  }
}
