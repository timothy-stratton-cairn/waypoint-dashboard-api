package com.cairn.waypoint.dashboard.endpoints.homeworktemplate;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.ExpectedResponseDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.ExpectedResponseDetailsListDto;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.TriggeredProtocolTemplateDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.UpdateHomeworkQuestionDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.UpdateHomeworkTemplateDetailsDto;
import com.cairn.waypoint.dashboard.entity.ExpectedResponse;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.HomeworkTemplate;
import com.cairn.waypoint.dashboard.entity.HomeworkTemplateLinkedHomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.enumeration.QuestionTypeEnum;
import com.cairn.waypoint.dashboard.service.data.ExpectedResponseDataService;
import com.cairn.waypoint.dashboard.service.data.HomeworkQuestionDataService;
import com.cairn.waypoint.dashboard.service.data.HomeworkTemplateDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolTemplateDataService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
//@RestController
//@Tag(name = "Homework Template")
public class UpdateHomeworkTemplateEndpoint {

  public static final String PATH = "/api/homework-template";

  private final HomeworkTemplateDataService homeworkTemplateDataService;
  private final HomeworkQuestionDataService homeworkQuestionDataService;
  private final ExpectedResponseDataService expectedResponseDataService;
  private final ProtocolTemplateDataService protocolTemplateDataService;

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  public UpdateHomeworkTemplateEndpoint(HomeworkTemplateDataService homeworkTemplateDataService,
      HomeworkQuestionDataService homeworkQuestionDataService,
      ExpectedResponseDataService expectedResponseDataService,
      ProtocolTemplateDataService protocolTemplateDataService) {
    this.homeworkTemplateDataService = homeworkTemplateDataService;
    this.homeworkQuestionDataService = homeworkQuestionDataService;
    this.expectedResponseDataService = expectedResponseDataService;
    this.protocolTemplateDataService = protocolTemplateDataService;
  }

  //TODO This class should be implemented but I think I might instead do a breakdown into questions as opposed to a full overall implementation
//  @Transactional
//  @PatchMapping(PATH)
//  @PreAuthorize("hasAnyAuthority('SCOPE_homework.template.full', 'SCOPE_admin.full')")
//  @Operation(
//      summary = "Allows a user to update an existing homework template",
//      description = "Allows a user to update an existing homework template."
//          + " Requires the `homework.template.full` permission.",
//      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
//      responses = {
//          @ApiResponse(responseCode = "200",
//              description = "Updated - Homework Template creation was successful",
//              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
//                  schema = @Schema(implementation = String.class))}),
//          @ApiResponse(responseCode = "400", description = "Bad Request",
//              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
//                  schema = @Schema(implementation = ErrorMessage.class))}),
//          @ApiResponse(responseCode = "401", description = "Unauthorized",
//              content = {@Content(schema = @Schema(hidden = true))}),
//          @ApiResponse(responseCode = "403", description = "Forbidden",
//              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
//                  schema = @Schema(implementation = ErrorMessage.class))}),
//          @ApiResponse(responseCode = "404", description = "Not Found",
//              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
//                  schema = @Schema(implementation = ErrorMessage.class))}),
//          @ApiResponse(responseCode = "409", description = "Conflict",
//              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
//                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> addHomeworkTemplate(@PathVariable Long homeworkTemplateId,
      @RequestBody UpdateHomeworkTemplateDetailsDto updateHomeworkTemplateDetailsDto,
      Principal principal) {
    log.info("User [{}] is attempting to update Homework Template with Name [{}]",
        principal.getName(), updateHomeworkTemplateDetailsDto.getName());

    Optional<HomeworkTemplate> homeworkTemplateToUpdateOptional = homeworkTemplateDataService.getHomeworkTemplateById(
        homeworkTemplateId);
    Set<ConstraintViolation<UpdateHomeworkTemplateDetailsDto>> violations = validator.validate(
        updateHomeworkTemplateDetailsDto);

    if (homeworkTemplateToUpdateOptional.isEmpty()) {
      return generateFailureResponse("Homework Template with ID [" +
              homeworkTemplateId + "] does not exist",
          HttpStatus.NOT_FOUND);
    } else if (!violations.isEmpty()) {
      return generateFailureResponse(
          violations.stream().map(ConstraintViolation::getMessage).collect(
              Collectors.joining(", ")), HttpStatus.BAD_REQUEST);
    } else if (this.homeworkTemplateDataService.findHomeworkTemplateByName(
        updateHomeworkTemplateDetailsDto.getName()).isPresent() &&
        !homeworkTemplateToUpdateOptional.get().getName()
            .equals(updateHomeworkTemplateDetailsDto.getName())) {
      return generateFailureResponse("Homework Template with name [" +
              updateHomeworkTemplateDetailsDto.getName() + "] already exist",
          HttpStatus.CONFLICT);
    } else {
      HomeworkTemplate updatedHomeworkTemplate = updateHomeworkTemplate(
          homeworkTemplateToUpdateOptional.get(), updateHomeworkTemplateDetailsDto,
          principal.getName());

      Function<Long, HomeworkQuestion> getHomeworkQuestionById = (questionId) -> updatedHomeworkTemplate.getHomeworkQuestions()
          .stream()
          .map(HomeworkTemplateLinkedHomeworkQuestion::getHomeworkQuestion)
          .filter(homeworkQuestion -> homeworkQuestion.getId().equals(questionId))
          .findFirst().orElseThrow();

      Function<Long, HomeworkTemplateLinkedHomeworkQuestion> getHomeworkTemplateLinkedHomeworkQuestionById =
          (questionId) -> updatedHomeworkTemplate.getHomeworkQuestions().stream()
              .filter(
                  homeworkTemplateLinkedHomeworkQuestion -> homeworkTemplateLinkedHomeworkQuestion.getHomeworkQuestion()
                      .getId().equals(questionId))
              .findFirst().orElseThrow();

      Set<HomeworkTemplateLinkedHomeworkQuestion> homeworkQuestionSet = new LinkedHashSet<>();
      AtomicInteger questionOrderIndex = new AtomicInteger(0);

      for (UpdateHomeworkQuestionDetailsDto updateHomeworkQuestionDetailsDto : updateHomeworkTemplateDetailsDto.getHomeworkQuestions()) {
        Optional<ProtocolTemplate> protocolTemplateOptional = Optional.ofNullable(null);

        if (updateHomeworkQuestionDetailsDto.getTriggeredProtocolId() != null &&
            (protocolTemplateOptional = this.protocolTemplateDataService.getProtocolTemplateById(
                updateHomeworkQuestionDetailsDto.getTriggeredProtocolId())).isEmpty()) {
          return generateFailureResponse("Protocol Template with ID [" +
                  updateHomeworkQuestionDetailsDto.getTriggeredProtocolId() + "] does not exist",
              HttpStatus.NOT_FOUND);
        } else if (Arrays.stream(QuestionTypeEnum.values()).noneMatch(
            questionType -> questionType.equals(
                updateHomeworkQuestionDetailsDto.getQuestionType()))) {
          return generateFailureResponse("Provided Question Type [" +
                  updateHomeworkQuestionDetailsDto.getQuestionType() + "] does not exist",
              HttpStatus.NOT_FOUND);
        } else {
          HomeworkQuestion homeworkQuestionToUpdate;
          HomeworkTemplateLinkedHomeworkQuestion homeworkTemplateLinkedHomeworkQuestionToUpdate;
          try {
            homeworkQuestionToUpdate = getHomeworkQuestionById.apply(
                updateHomeworkQuestionDetailsDto.getHomeworkQuestionId());
            homeworkTemplateLinkedHomeworkQuestionToUpdate = getHomeworkTemplateLinkedHomeworkQuestionById.apply(
                updateHomeworkQuestionDetailsDto.getHomeworkQuestionId());
          } catch (Exception e) {
            return generateFailureResponse("Homework Question with ID [" +
                    updateHomeworkQuestionDetailsDto.getHomeworkQuestionId() + "] does not exist",
                HttpStatus.NOT_FOUND);
          }

          HomeworkQuestion updatedHomeworkQuestion = updateHomeworkQuestionFields(
              homeworkQuestionToUpdate,
              updateHomeworkQuestionDetailsDto, protocolTemplateOptional.orElse(null),
              principal.getName());

          homeworkTemplateLinkedHomeworkQuestionToUpdate.setHomeworkQuestion(
              updatedHomeworkQuestion);

          homeworkQuestionSet.add(homeworkTemplateLinkedHomeworkQuestionToUpdate);
        }
      }

      updatedHomeworkTemplate.setHomeworkQuestions(homeworkQuestionSet);

      this.homeworkTemplateDataService.saveHomeworkTemplate(updatedHomeworkTemplate);

      log.info("Homework Template with ID [{}] was successfully created",
          updatedHomeworkTemplate.getId());
      return ResponseEntity.status(HttpStatus.CREATED)
          .body("");
//              updatedHomeworkTemplate.builder()
//                  .id(createdHomeworkTemplate.getId())
//                  .name(createdHomeworkTemplate.getName())
//                  .description(createdHomeworkTemplate.getDescription())
//                  .isMultiResponse(createdHomeworkTemplate.getMultiResponse())
//                  .homeworkQuestions(HomeworkQuestionDetailsListDto.builder()
//                      .questions(createdHomeworkTemplate.getHomeworkQuestions().stream()
//                          .map(
//                              homeworkTemplateLinkedHomeworkQuestion -> HomeworkQuestionDetailsDto.builder()
//                                  .questionAbbreviation(
//                                      homeworkTemplateLinkedHomeworkQuestion.getHomeworkQuestion()
//                                          .getQuestionAbbreviation())
//                                  .question(
//                                      homeworkTemplateLinkedHomeworkQuestion.getHomeworkQuestion()
//                                          .getQuestion())
//                                  .questionType(
//                                      homeworkTemplateLinkedHomeworkQuestion.getHomeworkQuestion()
//                                          .getQuestionType().name())
//                                  .isRequired(
//                                      homeworkTemplateLinkedHomeworkQuestion.getHomeworkQuestion()
//                                          .getRequired())
//                                  .expectedHomeworkResponses(this.getExpectedResponseDetailsListDto(
//                                      homeworkTemplateLinkedHomeworkQuestion.getHomeworkQuestion()
//                                          .getExpectedHomeworkResponses()))
//                                  .triggersProtocolCreation(
//                                      homeworkTemplateLinkedHomeworkQuestion.getHomeworkQuestion()
//                                          .getTriggersProtocolCreation())
//                                  .triggeringResponse(this.getExpectedResponseDetailsDto(
//                                      homeworkTemplateLinkedHomeworkQuestion.getHomeworkQuestion()
//                                          .getTriggeringResponse()))
//                                  .triggeredProtocol(this.getTriggeredProtocol(
//                                      homeworkTemplateLinkedHomeworkQuestion.getHomeworkQuestion()
//                                          .getTriggeredProtocol()))
//                                  .build())
//                          .toList())
//                      .build())
//                  .build());
    }
  }

  private ExpectedResponseDetailsListDto getExpectedResponseDetailsListDto(
      List<ExpectedResponse> expectedResponses) {
    if (expectedResponses == null || expectedResponses.isEmpty()) {
      return null;
    } else {
      return ExpectedResponseDetailsListDto.builder()
          .responses(expectedResponses.stream()
              .map(expectedResponse -> ExpectedResponseDetailsDto.builder()
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
          .response(expectedResponse.getResponse())
          .tooltip(expectedResponse.getTooltip())
          .build();
    }
  }

  private TriggeredProtocolTemplateDetailsDto getTriggeredProtocol(
      ProtocolTemplate protocolTemplate) {
    if (Objects.isNull(protocolTemplate)) {
      return null;
    } else {
      return TriggeredProtocolTemplateDetailsDto.builder()
          .id(protocolTemplate.getId())
          .name(protocolTemplate.getName())
          .description(protocolTemplate.getDescription())
          .build();
    }
  }

  private HomeworkTemplate updateHomeworkTemplate(HomeworkTemplate homeworkTemplateToUpdate,
      UpdateHomeworkTemplateDetailsDto updateHomeworkTemplateDetailsDto, String modifiedBy) {

    if (updateHomeworkTemplateDetailsDto.getName() != null
        && !updateHomeworkTemplateDetailsDto.getName().isEmpty()) {
      homeworkTemplateToUpdate.setName(updateHomeworkTemplateDetailsDto.getName());
    }

    if (updateHomeworkTemplateDetailsDto.getDescription() != null
        && !updateHomeworkTemplateDetailsDto.getDescription().isEmpty()) {
      homeworkTemplateToUpdate.setDescription(updateHomeworkTemplateDetailsDto.getDescription());
    }

    if (updateHomeworkTemplateDetailsDto.getIsMultiResponse() != null) {
      homeworkTemplateToUpdate.setMultiResponse(
          updateHomeworkTemplateDetailsDto.getIsMultiResponse());
    }

    homeworkTemplateToUpdate.setModifiedBy(modifiedBy);

    return this.homeworkTemplateDataService.saveHomeworkTemplate(homeworkTemplateToUpdate);
  }

  public HomeworkQuestion updateHomeworkQuestionFields(HomeworkQuestion homeworkQuestionToUpdate,
      UpdateHomeworkQuestionDetailsDto updateHomeworkQuestionDetailsDto,
      ProtocolTemplate protocolTemplate, String modifiedBy) {
    HomeworkQuestion createdHomeworkQuestion = saveHomeworkQuestion(homeworkQuestionToUpdate,
        updateHomeworkQuestionDetailsDto, protocolTemplate, modifiedBy);

    if (updateHomeworkQuestionDetailsDto.isValidSelectOptionRequest()
        && updateHomeworkQuestionDetailsDto.getResponseOptions() != null
        && !updateHomeworkQuestionDetailsDto.getResponseOptions().isEmpty()) {
      List<ExpectedResponse> expectedResponses = generateAndSaveExpectedResponses(
          updateHomeworkQuestionDetailsDto,
          modifiedBy);
      updateExpectedResponsesWithParentHomeworkQuestion(expectedResponses,
          createdHomeworkQuestion);

      if (updateHomeworkQuestionDetailsDto.getTriggerProtocolCreation()) {
        saveTriggeringExpectedResponse(createdHomeworkQuestion, expectedResponses,
            updateHomeworkQuestionDetailsDto);
      }
    }

    return createdHomeworkQuestion;
  }

  private List<ExpectedResponse> generateAndSaveExpectedResponses(
      UpdateHomeworkQuestionDetailsDto updateHomeworkQuestionDetailsDto, String modifiedBy) {
    List<ExpectedResponse> expectedResponses = new ArrayList<>();
    AtomicInteger responseOrdinalIndices = new AtomicInteger(0);

    updateHomeworkQuestionDetailsDto.getResponseOptions().stream()
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

  private HomeworkQuestion saveHomeworkQuestion(HomeworkQuestion homeworkQuestionToUpdate,
      UpdateHomeworkQuestionDetailsDto updateHomeworkQuestionDetailsDto,
      ProtocolTemplate protocolTemplate, String modifiedBy) {

    if (updateHomeworkQuestionDetailsDto.getQuestionAbbr() != null
        && !updateHomeworkQuestionDetailsDto.getQuestionAbbr().isEmpty()) {
      homeworkQuestionToUpdate.setQuestionAbbreviation(
          updateHomeworkQuestionDetailsDto.getQuestionAbbr());
    }

    if (updateHomeworkQuestionDetailsDto.getQuestion() != null
        && !updateHomeworkQuestionDetailsDto.getQuestion().isEmpty()) {
      homeworkQuestionToUpdate.setQuestion(
          updateHomeworkQuestionDetailsDto.getQuestion());
    }

    if (updateHomeworkQuestionDetailsDto.getQuestionType() != null) {
      homeworkQuestionToUpdate.setQuestionType(
          updateHomeworkQuestionDetailsDto.getQuestionType());
    }

    if (updateHomeworkQuestionDetailsDto.getIsRequired() != null) {
      homeworkQuestionToUpdate.setRequired(updateHomeworkQuestionDetailsDto.getIsRequired());
    }

    if (updateHomeworkQuestionDetailsDto.getTriggerProtocolCreation() != null) {
      homeworkQuestionToUpdate.setTriggersProtocolCreation(
          updateHomeworkQuestionDetailsDto.getTriggerProtocolCreation());
    }

    if (updateHomeworkQuestionDetailsDto.getIsRequired() != null) {
      homeworkQuestionToUpdate.setRequired(updateHomeworkQuestionDetailsDto.getIsRequired());
    }

    if (updateHomeworkQuestionDetailsDto.getTriggeredProtocolId() != null) {
      Optional<ProtocolTemplate> associatedProtocolTemplate = protocolTemplateDataService.getProtocolTemplateById(
          updateHomeworkQuestionDetailsDto.getTriggeredProtocolId());
      homeworkQuestionToUpdate.setTriggeredProtocol(associatedProtocolTemplate.get());
    }

    if (updateHomeworkQuestionDetailsDto.getIsRequired() != null) {
      homeworkQuestionToUpdate.setRequired(updateHomeworkQuestionDetailsDto.getIsRequired());
    }

//    HomeworkQuestion homeworkQuestion = HomeworkQuestion.builder()
//        .modifiedBy(modifiedBy)
//        .questionAbbreviation(addHomeworkQuestionDetailsDto.getQuestionAbbr())
//        .question(addHomeworkQuestionDetailsDto.getQuestion())
//        .questionType(addHomeworkQuestionDetailsDto.getQuestionType())
//        .required(addHomeworkQuestionDetailsDto.getIsRequired())
//        .triggersProtocolCreation(addHomeworkQuestionDetailsDto.getTriggerProtocolCreation())
//        .triggeredProtocol(protocolTemplate)
//        .build();

    return this.homeworkQuestionDataService.saveHomeworkQuestion(homeworkQuestionToUpdate);
  }

  private void updateExpectedResponsesWithParentHomeworkQuestion(
      List<ExpectedResponse> expectedResponses, HomeworkQuestion homeworkQuestion) {
    expectedResponses.stream()
        .peek(response -> response.setParentHomeworkQuestion(homeworkQuestion))
        .forEach(this.expectedResponseDataService::saveExpectedResponse);
  }

  private void saveTriggeringExpectedResponse(HomeworkQuestion homeworkQuestion,
      List<ExpectedResponse> expectedResponses,
      UpdateHomeworkQuestionDetailsDto updateHomeworkQuestionDetailsDto) {
    homeworkQuestion.setTriggeringResponse(
        expectedResponses.stream()
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
