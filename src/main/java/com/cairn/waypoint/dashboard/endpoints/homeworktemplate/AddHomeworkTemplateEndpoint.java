package com.cairn.waypoint.dashboard.endpoints.homeworktemplate;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.AddHomeworkQuestionDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.AddHomeworkTemplateDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.HomeworkTemplateDetailsDto;
import com.cairn.waypoint.dashboard.entity.ExpectedResponse;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.HomeworkTemplate;
import com.cairn.waypoint.dashboard.entity.HomeworkTemplateLinkedHomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.enumeration.QuestionTypeEnum;
import com.cairn.waypoint.dashboard.entity.enumeration.TemplateStatusEnum;
import com.cairn.waypoint.dashboard.service.data.ExpectedResponseDataService;
import com.cairn.waypoint.dashboard.service.data.HomeworkQuestionDataService;
import com.cairn.waypoint.dashboard.service.data.HomeworkTemplateDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolTemplateDataService;
import com.cairn.waypoint.dashboard.service.helper.HomeworkTemplateHelperService;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
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
@Tag(name = "Homework Template")
public class AddHomeworkTemplateEndpoint {

  public static final String PATH = "/api/homework-template";

  private final HomeworkTemplateDataService homeworkTemplateDataService;
  private final HomeworkQuestionDataService homeworkQuestionDataService;
  private final ExpectedResponseDataService expectedResponseDataService;
  private final ProtocolTemplateDataService protocolTemplateDataService;

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  public AddHomeworkTemplateEndpoint(HomeworkTemplateDataService homeworkTemplateDataService,
      HomeworkQuestionDataService homeworkQuestionDataService,
      ExpectedResponseDataService expectedResponseDataService,
      ProtocolTemplateDataService protocolTemplateDataService) {
    this.homeworkTemplateDataService = homeworkTemplateDataService;
    this.homeworkQuestionDataService = homeworkQuestionDataService;
    this.expectedResponseDataService = expectedResponseDataService;
    this.protocolTemplateDataService = protocolTemplateDataService;
  }

  @Transactional
  @PostMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_homework.template.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Allows a user to create a new homework template",
      description = "Allows a user to create a new homework template."
          + " Requires the `homework.template.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "201",
              description = "Created - Homework Template creation was successful",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = HomeworkTemplateDetailsDto.class))}),
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
  public ResponseEntity<?> addHomeworkTemplate(
      @RequestBody AddHomeworkTemplateDetailsDto addHomeworkTemplateDetailsDto,
      Principal principal) {
    log.info("User [{}] is attempting to create Homework Template with Name [{}]",
        principal.getName(), addHomeworkTemplateDetailsDto.getName());

    Set<ConstraintViolation<AddHomeworkTemplateDetailsDto>> violations = validator.validate(
        addHomeworkTemplateDetailsDto);

    if (!violations.isEmpty()) {
      return generateFailureResponse(
          violations.stream().map(ConstraintViolation::getMessage).collect(
              Collectors.joining(", ")), HttpStatus.BAD_REQUEST);
    } else if (this.homeworkTemplateDataService.findHomeworkTemplateByName(
        addHomeworkTemplateDetailsDto.getName()).isPresent()) {
      return generateFailureResponse("Homework Template with name [" +
              addHomeworkTemplateDetailsDto.getName() + "] already exist",
          HttpStatus.CONFLICT);
    } else {
      HomeworkTemplate createdHomeworkTemplate = createHomeworkTemplate(
          addHomeworkTemplateDetailsDto,
          principal.getName());

      Set<HomeworkTemplateLinkedHomeworkQuestion> homeworkQuestionSet = new LinkedHashSet<>();
      AtomicInteger questionOrderIndex = new AtomicInteger(0);

      for (AddHomeworkQuestionDetailsDto addHomeworkQuestionDetailsDto : addHomeworkTemplateDetailsDto.getHomeworkQuestions()) {
        Optional<ProtocolTemplate> protocolTemplateOptional = Optional.ofNullable(null);

        if (addHomeworkQuestionDetailsDto.getTriggeredProtocolId() != null &&
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

          homeworkQuestionSet.add(HomeworkTemplateLinkedHomeworkQuestion.builder()
              .modifiedBy(principal.getName())
              .homeworkTemplate(createdHomeworkTemplate)
              .homeworkQuestion(createdHomeworkQuestion)
              .ordinalIndex(questionOrderIndex.getAndIncrement())
              .build());
        }
      }

      createdHomeworkTemplate.setHomeworkQuestions(homeworkQuestionSet);

      this.homeworkTemplateDataService.saveHomeworkTemplate(createdHomeworkTemplate);

      log.info("Homework Template with ID [{}] was successfully created",
          createdHomeworkTemplate.getId());
      return ResponseEntity.status(HttpStatus.CREATED)
          .body(
              HomeworkTemplateHelperService.getHomeworkTemplateDetailsDto(createdHomeworkTemplate));
    }
  }

  private HomeworkTemplate createHomeworkTemplate(
      AddHomeworkTemplateDetailsDto addHomeworkTemplateDetailsDto, String modifiedBy) {
    return this.homeworkTemplateDataService.saveHomeworkTemplate(HomeworkTemplate.builder()
        .modifiedBy(modifiedBy)
        .name(addHomeworkTemplateDetailsDto.getName())
        .description(addHomeworkTemplateDetailsDto.getDescription())
        .multiResponse(addHomeworkTemplateDetailsDto.getIsMultiResponse())
        .status(TemplateStatusEnum.INACTIVE)
        .build());
  }

  public HomeworkQuestion createHomeworkQuestion(
      AddHomeworkQuestionDetailsDto addHomeworkQuestionDetailsDto,
      ProtocolTemplate protocolTemplate, String modifiedBy) {
    HomeworkQuestion createdHomeworkQuestion = saveHomeworkQuestion(
        addHomeworkQuestionDetailsDto, protocolTemplate, modifiedBy);

    if (addHomeworkQuestionDetailsDto.isValidSelectOptionRequest()
        && addHomeworkQuestionDetailsDto.getResponseOptions() != null
        && !addHomeworkQuestionDetailsDto.getResponseOptions().isEmpty()) {
      List<ExpectedResponse> expectedResponses = generateAndSaveExpectedResponses(
          addHomeworkQuestionDetailsDto,
          modifiedBy);
      updateExpectedResponsesWithParentHomeworkQuestion(expectedResponses,
          createdHomeworkQuestion);

      if (addHomeworkQuestionDetailsDto.getTriggerProtocolCreation()) {
        saveTriggeringExpectedResponse(createdHomeworkQuestion, expectedResponses,
            addHomeworkQuestionDetailsDto);
      }
    }

    return createdHomeworkQuestion;
  }

  private List<ExpectedResponse> generateAndSaveExpectedResponses(
      AddHomeworkQuestionDetailsDto addHomeworkQuestionDetailsDto, String modifiedBy) {
    List<ExpectedResponse> expectedResponses = new ArrayList<>();
    AtomicInteger responseOrdinalIndices = new AtomicInteger(0);

    addHomeworkQuestionDetailsDto.getResponseOptions().stream()
        .map(responseOption -> ExpectedResponse.builder()
            .modifiedBy(modifiedBy)
            .response(responseOption.getResponse())
            .tooltip(responseOption.getTooltip())
            .ordinalIndex(responseOrdinalIndices.getAndIncrement())
            .build())
        .peek(this.expectedResponseDataService::saveExpectedResponse)
        .forEach(expectedResponses::add);

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
        .status(TemplateStatusEnum.INACTIVE)
        .triggeredProtocol(protocolTemplate)
        .build();

    return this.homeworkQuestionDataService.saveHomeworkQuestion(homeworkQuestion);
  }

  private void updateExpectedResponsesWithParentHomeworkQuestion(
      List<ExpectedResponse> expectedResponses, HomeworkQuestion homeworkQuestion) {
    expectedResponses.stream()
        .peek(response -> response.setParentHomeworkQuestion(homeworkQuestion))
        .forEach(this.expectedResponseDataService::saveExpectedResponse);
  }

  private void saveTriggeringExpectedResponse(HomeworkQuestion homeworkQuestion,
      List<ExpectedResponse> expectedResponses,
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
