package com.cairn.waypoint.dashboard.endpoints.homeworkquestion;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.homeworkquestion.dto.UpdateHomeworkQuestionDetailsDto;
import com.cairn.waypoint.dashboard.entity.ExpectedResponse;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.enumeration.QuestionTypeEnum;
import com.cairn.waypoint.dashboard.service.data.ExpectedResponseDataService;
import com.cairn.waypoint.dashboard.service.data.HomeworkQuestionDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolTemplateDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Homework Question")
public class UpdateHomeworkQuestionEndpoint {

  public static final String PATH = "/api/homework-question/{homeworkQuestionId}";
  private final HomeworkQuestionDataService homeworkQuestionDataService;

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
  private final ProtocolTemplateDataService protocolTemplateDataService;
  private final ExpectedResponseDataService expectedResponseDataService;

  public UpdateHomeworkQuestionEndpoint(HomeworkQuestionDataService homeworkQuestionDataService,
      ProtocolTemplateDataService protocolTemplateDataService,
      ExpectedResponseDataService expectedResponseDataService) {
    this.homeworkQuestionDataService = homeworkQuestionDataService;
    this.protocolTemplateDataService = protocolTemplateDataService;
    this.expectedResponseDataService = expectedResponseDataService;
  }

  @PatchMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_homework.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Updates a homework question by the provided homework question ID.",
      description = "Updates a homework question by the provided homework question ID. Requires the `homework.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                  schema = @Schema(implementation = String.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> getHomeworkQuestionById(@PathVariable Long homeworkQuestionId,
      @RequestBody UpdateHomeworkQuestionDetailsDto updateHomeworkQuestionDetailsDto,
      Principal principal) {
    log.info("User [{}] is Updating Homework Question with ID [{}]", principal.getName(),
        homeworkQuestionId);
    Optional<HomeworkQuestion> homeworkQuestionToUpdateOptional = this.homeworkQuestionDataService.getHomeworkQuestionById(
        homeworkQuestionId);
    Optional<ProtocolTemplate> protocolTemplateOptional = Optional.empty();

    Set<ConstraintViolation<UpdateHomeworkQuestionDetailsDto>> violations = validator.validate(
        updateHomeworkQuestionDetailsDto);

    if (homeworkQuestionToUpdateOptional.isEmpty()) {
      return generateFailureResponse("Homework Question [" +
              homeworkQuestionId + "] does not exist",
          HttpStatus.NOT_FOUND);
    } else if (!violations.isEmpty()) {
      return generateFailureResponse(
          violations.stream().map(ConstraintViolation::getMessage).collect(
              Collectors.joining(", ")), HttpStatus.BAD_REQUEST);
    } else if (this.homeworkQuestionDataService.findByQuestion(
        updateHomeworkQuestionDetailsDto.getQuestion()).isPresent() &&
        !homeworkQuestionToUpdateOptional.get().getQuestion()
            .equals(updateHomeworkQuestionDetailsDto.getQuestion())) {
      return generateFailureResponse("Homework Question [" +
              updateHomeworkQuestionDetailsDto.getQuestion() + "] already exist",
          HttpStatus.CONFLICT);
    } else if (updateHomeworkQuestionDetailsDto.getTriggeredProtocolId() != null &&
        (protocolTemplateOptional = this.protocolTemplateDataService.getProtocolTemplateById(
            updateHomeworkQuestionDetailsDto.getTriggeredProtocolId())).isEmpty()) {
      return generateFailureResponse("Protocol Template with ID [" +
              updateHomeworkQuestionDetailsDto.getTriggeredProtocolId() + "] does not exist",
          HttpStatus.NOT_FOUND);
    } else if (Arrays.stream(QuestionTypeEnum.values()).noneMatch(
        questionType -> questionType.equals(updateHomeworkQuestionDetailsDto.getQuestionType()))) {
      return generateFailureResponse("Provided Question Type [" +
              updateHomeworkQuestionDetailsDto.getQuestionType() + "] does not exist",
          HttpStatus.NOT_FOUND);
    } else {
      HomeworkQuestion homeworkQuestionToUpdate = homeworkQuestionToUpdateOptional.get();

      if (updateHomeworkQuestionDetailsDto.getQuestionAbbr() != null) {
        homeworkQuestionToUpdate.setQuestionAbbreviation(
            updateHomeworkQuestionDetailsDto.getQuestionAbbr());
      }

      if (updateHomeworkQuestionDetailsDto.getQuestion() != null) {
        homeworkQuestionToUpdate.setQuestion(updateHomeworkQuestionDetailsDto.getQuestion());
      }

      if (updateHomeworkQuestionDetailsDto.getQuestionType() != null) {
        homeworkQuestionToUpdate.setQuestionType(
            updateHomeworkQuestionDetailsDto.getQuestionType());
      }

      if (updateHomeworkQuestionDetailsDto.getIsRequired() != null) {
        homeworkQuestionToUpdate.setRequired(updateHomeworkQuestionDetailsDto.getIsRequired());
      }

      if (updateHomeworkQuestionDetailsDto.getStatus() != null) {
        homeworkQuestionToUpdate.setStatus(updateHomeworkQuestionDetailsDto.getStatus());
      }

      protocolTemplateOptional.ifPresent(homeworkQuestionToUpdate::setTriggeredProtocol);

      if (updateHomeworkQuestionDetailsDto.isValidSelectOptionRequest()
          && updateHomeworkQuestionDetailsDto.getResponseOptions() != null
          && !updateHomeworkQuestionDetailsDto.getResponseOptions().isEmpty()) {
        homeworkQuestionToUpdate.getExpectedHomeworkResponses().forEach(expectedResponse -> {
          expectedResponse.setActive(Boolean.FALSE);
          expectedResponse.setModifiedBy(principal.getName());
        });

        updateHomeworkQuestionDetailsDto.getResponseOptions().forEach(expectedResponseToUpdate -> {
          AtomicInteger ordinalIndex = new AtomicInteger(0);
          Optional<ExpectedResponse> expectedResponseOnHomeworkQuestion;
          if ((expectedResponseOnHomeworkQuestion = homeworkQuestionToUpdate.getExpectedHomeworkResponses()
              .stream()
              .filter(expectedResponse -> expectedResponse.getId()
                  .equals(expectedResponseToUpdate.getExpectedResponseId()))
              .findFirst()).isPresent()) {
            expectedResponseOnHomeworkQuestion.get().setModifiedBy(principal.getName());
            expectedResponseOnHomeworkQuestion.get()
                .setResponse(expectedResponseToUpdate.getResponse());
            expectedResponseOnHomeworkQuestion.get()
                .setTooltip(expectedResponseToUpdate.getTooltip());
            expectedResponseOnHomeworkQuestion.get().setActive(Boolean.TRUE);
            expectedResponseOnHomeworkQuestion.get()
                .setParentHomeworkQuestion(homeworkQuestionToUpdate);
            expectedResponseOnHomeworkQuestion.get()
                .setOrdinalIndex(ordinalIndex.getAndIncrement());
          } else {
            expectedResponseOnHomeworkQuestion = Optional.of(ExpectedResponse.builder()
                .modifiedBy(principal.getName())
                .response(expectedResponseToUpdate.getResponse())
                .tooltip(expectedResponseToUpdate.getTooltip())
                .active(Boolean.TRUE)
                .parentHomeworkQuestion(homeworkQuestionToUpdate)
                .ordinalIndex(ordinalIndex.getAndIncrement())
                .build());

          }
          ExpectedResponse upsertedExpectedResponse = expectedResponseDataService.saveExpectedResponse(
              expectedResponseOnHomeworkQuestion.get());
          homeworkQuestionToUpdate.getExpectedHomeworkResponses().add(upsertedExpectedResponse);
        });

        saveTriggeringExpectedResponse(homeworkQuestionToUpdate, updateHomeworkQuestionDetailsDto);
      }

      HomeworkQuestion updatedHomeworkQuestion = homeworkQuestionDataService.saveHomeworkQuestion(
          homeworkQuestionToUpdate);

      log.info("Homework Question with ID [{}] successfully updated",
          updatedHomeworkQuestion.getId());
      return ResponseEntity.ok("Homework Question with ID [" + updatedHomeworkQuestion.getId()
          + "] successfully updated");
    }
  }

  private void saveTriggeringExpectedResponse(HomeworkQuestion homeworkQuestion,
      UpdateHomeworkQuestionDetailsDto updateHomeworkQuestionDetailsDto) {
    homeworkQuestion.setTriggeringResponse(
        homeworkQuestion.getExpectedHomeworkResponses().stream()
            .filter(response -> response.getResponse()
                .equals(updateHomeworkQuestionDetailsDto.getTriggeringResponse().getResponse()))
            .findFirst()
            .orElseThrow()
    );

    this.homeworkQuestionDataService.saveHomeworkQuestion(homeworkQuestion);
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
