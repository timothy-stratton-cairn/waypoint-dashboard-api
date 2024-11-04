package com.cairn.waypoint.dashboard.endpoints.homework;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.homework.dto.UpdateHomeworkResponseDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.homework.dto.UpdateHomeworkResponseDetailsListDto;
import com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto.HomeworkResponseListDto;
import com.cairn.waypoint.dashboard.entity.HomeworkResponse;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.service.data.HomeworkResponseDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
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

  public static final String PATH = "/api/protocol/{protocolId}";
  private final S3FileUpload s3FileUpload;
  private final ProtocolDataService protocolDataService;
  private final HomeworkResponseDataService responseDataService;

  @Value("${waypoint.dashboard.s3.homework-response-key-prefix}")
  private String homeworkResponseKeyPrefix;

  public UpdateHomeworkResponsesEndpoint(S3FileUpload s3FileUpload, ProtocolDataService protocolDataService,HomeworkResponseDataService responseDataService ) {
    this.s3FileUpload = s3FileUpload;
    this.responseDataService = responseDataService;
    this.protocolDataService = protocolDataService;
  }

  @PatchMapping(value = PATH, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @PreAuthorize("hasAnyAuthority('SCOPE_homework.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Updates the protocol question responses of the provided protocol ID.",
      description = "Updates the protocol question responses of the provided protocol ID. Requires the `homework.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200", description = "Updated - Protocol update was successful"),
          @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessage.class))})
      })
  
  public ResponseEntity<?> updateHomeworkResponses(@PathVariable Long protocolId,
      @RequestPart("json") @Parameter(schema = @Schema(type = "string", format = "binary")) UpdateHomeworkResponseDetailsListDto updateHomeworkResponseDetailsListDto,
      @RequestPart("files") Optional<MultipartFile[]> files, Principal principal) {

    Optional<Protocol> protocolToUpdate = protocolDataService.getProtocolById(protocolId);
    if (protocolToUpdate.isEmpty()) {
        return generateFailureResponse("Protocol with ID [" + protocolId + "] does not exist", HttpStatus.NOT_FOUND);
    }

    Map<Long, UpdateHomeworkResponseDetailsDto> homeworkQuestions = updateHomeworkResponseDetailsListDto.getResponses()
        .stream()
        .collect(Collectors.toMap(UpdateHomeworkResponseDetailsDto::getQuestionId, Function.identity()));

    HomeworkResponseListDto responseListDto = responseDataService.getHomeResponseByProtocol_Id(protocolId);
    if (responseListDto.getResponses().isEmpty()) {
        return generateFailureResponse("No associated Homework Responses found", HttpStatus.NOT_FOUND);
    }

    responseListDto = stageHomeworkResponses(homeworkQuestions, files.orElse(null), responseListDto, principal.getName());

    responseDataService.saveAll(responseListDto.getResponses());

    boolean allResponsesComplete = responseListDto.getResponses().stream()
        .noneMatch(response -> response.getHomeworkQuestion().getRequired() &&
                (response.getResponse() == null || response.getResponse().isEmpty()));

    if (allResponsesComplete) {
      protocolToUpdate.get().setCompletionDate(LocalDate.now());
    } else {
      protocolToUpdate.get().setCompletionDate(null);
    }

    protocolDataService.updateProtocol(protocolToUpdate.get());

    return ResponseEntity.status(HttpStatus.OK).body("Protocol responses updated successfully");
  }

  private HomeworkResponseListDto stageHomeworkResponses(
      Map<Long, UpdateHomeworkResponseDetailsDto> homeworkQuestions,
      final MultipartFile[] files, HomeworkResponseListDto responseListDto, String modifiedBy) {

    Function<String, Optional<MultipartFile>> fileProvider;
    if (files != null && files.length > 0 && files[0].getOriginalFilename() != null
        && !files[0].getOriginalFilename().isEmpty()) {
      fileProvider = filename -> Stream.of(files)
          .filter(file -> filename.equals(file.getOriginalFilename()))
          .findFirst();
    } else {
      fileProvider = filename -> Optional.empty();
    }

    responseListDto.getResponses().forEach(homeworkResponse -> {
      UpdateHomeworkResponseDetailsDto responseToUpdate = homeworkQuestions.get(
          homeworkResponse.getHomeworkQuestion().getId());

      if (responseToUpdate != null) {
        Optional<MultipartFile> fileToUpload = fileProvider.apply(responseToUpdate.getUserResponse());
        String uploadedFileName;

        try {
          if (fileToUpload.isPresent()) {
            uploadedFileName = s3FileUpload.uploadFile(fileToUpload.get(), modifiedBy, homeworkResponseKeyPrefix);
            responseToUpdate.setUserResponse(uploadedFileName);
            responseToUpdate.setUploadedFile(fileToUpload.get());
          }

          homeworkResponse.setModifiedBy(modifiedBy);
          homeworkResponse.setResponse(responseToUpdate.getUserResponse());
          homeworkResponse.setFileGuid(fileToUpload.isPresent() ? UUID.randomUUID().toString() : null);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    });

    return responseListDto;
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

