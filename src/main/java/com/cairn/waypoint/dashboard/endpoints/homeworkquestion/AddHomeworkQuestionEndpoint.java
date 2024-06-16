package com.cairn.waypoint.dashboard.endpoints.homeworkquestion;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.homeworkquestion.dto.AddHomeworkQuestionDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.homeworkquestion.dto.ExpectedResponseDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.homeworkquestion.dto.ExpectedResponseDetailsListDto;
import com.cairn.waypoint.dashboard.endpoints.homeworkquestion.dto.HomeworkQuestionDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.homeworkquestion.dto.ProtocolTemplateDetailsDto;
import com.cairn.waypoint.dashboard.entity.ExpectedResponse;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.enumeration.QuestionTypeEnum;
import com.cairn.waypoint.dashboard.entity.enumeration.TemplateStatusEnum;
import com.cairn.waypoint.dashboard.service.data.ExpectedResponseDataService;
import com.cairn.waypoint.dashboard.service.data.HomeworkQuestionDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolTemplateDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Homework Question")
public class AddHomeworkQuestionEndpoint {

  public static final String PATH = "/api/homework-question";

  private final HomeworkQuestionDataService homeworkQuestionDataService;
  private final ExpectedResponseDataService expectedResponseDataService;
  private final ProtocolTemplateDataService protocolTemplateDataService;

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  public AddHomeworkQuestionEndpoint(HomeworkQuestionDataService homeworkQuestionDataService,
      ExpectedResponseDataService expectedResponseDataService,
      ProtocolTemplateDataService protocolTemplateDataService) {
    this.homeworkQuestionDataService = homeworkQuestionDataService;
    this.expectedResponseDataService = expectedResponseDataService;
    this.protocolTemplateDataService = protocolTemplateDataService;
  }

  @Transactional
  @PostMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_homework.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Allows a user to create a new homework question",
      description = "Allows a user to create a new homework question."
          + " Requires the `homework.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "201",
              description = "Created - Homework Question creation was successful",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = HomeworkQuestionDetailsDto.class))}),
          @ApiResponse(responseCode = "400", description = "Bad Request",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "409", description = "Conflict",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> addHomeworkQuestion(
      @RequestBody AddHomeworkQuestionDetailsDto addHomeworkQuestionDetailsDto,
      Principal principal) {
    log.info("User [{}] is attempting to create Homework Question [{}]",
        principal.getName(), addHomeworkQuestionDetailsDto.getQuestion());
    Optional<ProtocolTemplate> protocolTemplateOptional = Optional.empty();

    Set<ConstraintViolation<AddHomeworkQuestionDetailsDto>> violations = validator.validate(
        addHomeworkQuestionDetailsDto);

    if (!violations.isEmpty()) {
      return generateFailureResponse(
          violations.stream().map(ConstraintViolation::getMessage).collect(
              Collectors.joining(", ")), HttpStatus.BAD_REQUEST);
    } else if (this.homeworkQuestionDataService.findByQuestion(
        addHomeworkQuestionDetailsDto.getQuestion()).isPresent()) {
      return generateFailureResponse("Homework Question [" +
              addHomeworkQuestionDetailsDto.getQuestion() + "] already exist",
          HttpStatus.CONFLICT);
    } else if (addHomeworkQuestionDetailsDto.getTriggeredProtocolId() != null &&
        (protocolTemplateOptional = this.protocolTemplateDataService.getProtocolTemplateById(
            addHomeworkQuestionDetailsDto.getTriggeredProtocolId())).isEmpty()) {
      return generateFailureResponse("Protocol Template with ID [" +
              addHomeworkQuestionDetailsDto.getTriggeredProtocolId() + "] does not exist",
          HttpStatus.NOT_FOUND);
    } else if (Arrays.stream(QuestionTypeEnum.values()).noneMatch(
        questionType -> questionType.equals(addHomeworkQuestionDetailsDto.getQuestionType()))) {
      return generateFailureResponse("Provided Question Type [" +
              addHomeworkQuestionDetailsDto.getQuestionType() + "] does not exist",
          HttpStatus.NOT_FOUND);
    } else {
      HomeworkQuestion createdHomeworkQuestion = createHomeworkQuestion(
          addHomeworkQuestionDetailsDto, protocolTemplateOptional.orElse(null),
          principal.getName());

      log.info("Homework Question with ID [{}] was successfully created",
          createdHomeworkQuestion.getId());

      return ResponseEntity.ok(HomeworkQuestionDetailsDto.builder()
          .questionId(createdHomeworkQuestion.getId())
          .questionAbbr(createdHomeworkQuestion.getQuestionAbbreviation())
          .question(createdHomeworkQuestion.getQuestion())
          .isRequired(createdHomeworkQuestion.getRequired())
          .status(createdHomeworkQuestion.getStatus().name())
          .questionType(createdHomeworkQuestion.getQuestionType().name())
          .triggersProtocolCreation(createdHomeworkQuestion.getTriggersProtocolCreation())
          .triggeredProtocol(getTriggeredProtocol(createdHomeworkQuestion.getTriggeredProtocol()))
          .expectedHomeworkResponses(getExpectedResponseDetailsListDto(
              createdHomeworkQuestion.getExpectedHomeworkResponses()))
          .triggeringResponse(
              getExpectedResponseDetailsDto(createdHomeworkQuestion.getTriggeringResponse()))
          .build());
    }
  }

  private ExpectedResponseDetailsListDto getExpectedResponseDetailsListDto(
      Set<ExpectedResponse> expectedResponses) {
    if (expectedResponses == null || expectedResponses.isEmpty()) {
      return null;
    } else {
      return ExpectedResponseDetailsListDto.builder()
          .responses(expectedResponses.stream()
              .map(expectedResponse -> ExpectedResponseDetailsDto.builder()
                  .id(expectedResponse.getId())
                  .response(expectedResponse.getResponse())
                  .tooltip(expectedResponse.getTooltip())
                  .build())
              .toList())
          .build();
    }
  }

  private ExpectedResponseDetailsDto getExpectedResponseDetailsDto(
      ExpectedResponse expectedResponse) {
    if (Objects.isNull(expectedResponse)) {
      return null;
    } else {
      return ExpectedResponseDetailsDto.builder()
          .id(expectedResponse.getId())
          .response(expectedResponse.getResponse())
          .tooltip(expectedResponse.getTooltip())
          .build();
    }
  }

  private ProtocolTemplateDetailsDto getTriggeredProtocol(
      ProtocolTemplate protocolTemplate) {
    if (Objects.isNull(protocolTemplate)) {
      return null;
    } else {
      return ProtocolTemplateDetailsDto.builder()
          .id(protocolTemplate.getId())
          .name(protocolTemplate.getName())
          .description(protocolTemplate.getDescription())
          .build();
    }
  }

  public HomeworkQuestion createHomeworkQuestion(
      AddHomeworkQuestionDetailsDto addHomeworkQuestionDetailsDto,
      ProtocolTemplate protocolTemplate, String modifiedBy) {
    HomeworkQuestion createdHomeworkQuestion = saveHomeworkQuestion(
        addHomeworkQuestionDetailsDto, protocolTemplate, modifiedBy);

    if (addHomeworkQuestionDetailsDto.isValidSelectOptionRequest()
        && addHomeworkQuestionDetailsDto.getResponseOptions() != null
        && !addHomeworkQuestionDetailsDto.getResponseOptions().isEmpty()) {
      Set<ExpectedResponse> expectedResponses = generateAndSaveExpectedResponses(
          createdHomeworkQuestion,
          addHomeworkQuestionDetailsDto,
          modifiedBy);

      if (addHomeworkQuestionDetailsDto.getTriggerProtocolCreation()) {
        saveTriggeringExpectedResponse(createdHomeworkQuestion, expectedResponses,
            addHomeworkQuestionDetailsDto);
      }
    }

    return homeworkQuestionDataService.getHomeworkQuestionById(createdHomeworkQuestion.getId())
        .get();
  }

  private Set<ExpectedResponse> generateAndSaveExpectedResponses(
      HomeworkQuestion createdHomeworkQuestion,
      AddHomeworkQuestionDetailsDto addHomeworkQuestionDetailsDto, String modifiedBy) {
    Set<ExpectedResponse> expectedResponses = new HashSet<>();
    AtomicInteger responseOrdinalIndices = new AtomicInteger(0);

    addHomeworkQuestionDetailsDto.getResponseOptions().stream()
        .map(responseOption -> ExpectedResponse.builder()
            .modifiedBy(modifiedBy)
            .response(responseOption.getResponse())
            .tooltip(responseOption.getTooltip())
            .ordinalIndex(responseOrdinalIndices.getAndIncrement())
            .parentHomeworkQuestion(createdHomeworkQuestion)
            .build())
        .peek(this.expectedResponseDataService::saveExpectedResponse)
        .forEach(expectedResponses::add);

    createdHomeworkQuestion.setExpectedHomeworkResponses(expectedResponses);

    return expectedResponses;
  }

  private HomeworkQuestion saveHomeworkQuestion(
      AddHomeworkQuestionDetailsDto addHomeworkQuestionDetailsDto,
      ProtocolTemplate protocolTemplate, String modifiedBy) {
    HomeworkQuestion homeworkQuestion = HomeworkQuestion.builder()
        .modifiedBy(modifiedBy)
        .questionAbbreviation(addHomeworkQuestionDetailsDto.getQuestionAbbr())
        .question(addHomeworkQuestionDetailsDto.getQuestion())
        .questionType(addHomeworkQuestionDetailsDto.getQuestionType())
        .required(addHomeworkQuestionDetailsDto.getIsRequired())
        .triggersProtocolCreation(addHomeworkQuestionDetailsDto.getTriggerProtocolCreation())
        .triggeredProtocol(protocolTemplate)
        .status(TemplateStatusEnum.INACTIVE)
        .build();

    return this.homeworkQuestionDataService.saveHomeworkQuestion(homeworkQuestion);
  }

//  private void updateExpectedResponsesWithParentHomeworkQuestion(
//      Set<ExpectedResponse> expectedResponses, HomeworkQuestion homeworkQuestion) {
//    expectedResponses.stream()
//        .peek(response -> response.setParentHomeworkQuestion(homeworkQuestion))
//        .forEach(this.expectedResponseDataService::saveExpectedResponse);
//  }

  private void saveTriggeringExpectedResponse(HomeworkQuestion homeworkQuestion,
      Set<ExpectedResponse> expectedResponses,
      AddHomeworkQuestionDetailsDto addHomeworkQuestionDetailsDto) {
    homeworkQuestion.setTriggeringResponse(
        expectedResponses.stream()
            .filter(response -> response.getResponse()
                .equals(addHomeworkQuestionDetailsDto.getTriggeringResponse().getResponse()))
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
