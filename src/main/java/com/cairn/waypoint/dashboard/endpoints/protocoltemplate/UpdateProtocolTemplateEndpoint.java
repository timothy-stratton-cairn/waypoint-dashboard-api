package com.cairn.waypoint.dashboard.endpoints.protocoltemplate;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.protocoltemplate.dto.UpdateProtocolTemplateDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.protocoltemplate.mapper.ProtocolTemplateMapper;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplateLinkedStepTemplate;
import com.cairn.waypoint.dashboard.entity.StepTemplate;
import com.cairn.waypoint.dashboard.entity.enumeration.TemplateStatusEnum;
import com.cairn.waypoint.dashboard.service.data.HomeworkDataService;
import com.cairn.waypoint.dashboard.service.data.HomeworkQuestionDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolStepLinkedHomeworkService;
import com.cairn.waypoint.dashboard.service.data.ProtocolTemplateDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolTemplateLinkedStepTemplateDataService;
import com.cairn.waypoint.dashboard.service.data.StepTemplateDataService;
import com.cairn.waypoint.dashboard.service.helper.ProtocolTemplateHelperService;
import com.cairn.waypoint.dashboard.utility.BeanUtility;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
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
@Tag(name = "Protocol Template")
public class UpdateProtocolTemplateEndpoint {

  public static final String PATH = "/api/protocol-template/{protocolTemplateId}";

  private final ProtocolTemplateDataService protocolTemplateDataService;
  private final StepTemplateDataService stepTemplateDataService;
  private final ProtocolTemplateLinkedStepTemplateDataService protocolTemplateLinkedStepTemplateDataService;

  private final ProtocolTemplateHelperService protocolTemplateHelperService;
  //private final HomeworkTemplateDataService homeworkTemplateDataService;
  private final HomeworkQuestionDataService homeworkQuestionDataService;

  public UpdateProtocolTemplateEndpoint(ProtocolTemplateDataService protocolTemplateDataService,
      StepTemplateDataService stepTemplateDataService,
      ProtocolTemplateLinkedStepTemplateDataService protocolTemplateLinkedStepTemplateDataService,
      ProtocolDataService protocolDataService, HomeworkDataService homeworkDataService,
      ProtocolStepLinkedHomeworkService protocolStepLinkedHomeworkService,
      //HomeworkTemplateDataService homeworkTemplateDataService,
      HomeworkQuestionDataService homeworkQuestionDataService) {
    this.protocolTemplateDataService = protocolTemplateDataService;
    this.stepTemplateDataService = stepTemplateDataService;
    this.protocolTemplateLinkedStepTemplateDataService = protocolTemplateLinkedStepTemplateDataService;

    this.protocolTemplateHelperService = new ProtocolTemplateHelperService(
        protocolDataService, stepTemplateDataService
        //, homeworkDataService,
        //protocolStepLinkedHomeworkService
    );
    //this.homeworkTemplateDataService = homeworkTemplateDataService;
    this.homeworkQuestionDataService = homeworkQuestionDataService;
  }

  @Transactional
  @PatchMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.template.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Allows a user to update a protocol template, "
          + "updating properties and the provided protocol step templates to associated protocol template.",
      description = "Allows a user to update a protocol template, "
          + "updating properties and the provided protocol step templates to associated protocol template."
          + " Requires the `protocol.template.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              description = "Updated - Protocol Template update was successful"),
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
  public ResponseEntity<?> updateProtocolTemplate(@PathVariable Long protocolTemplateId,
      @RequestBody UpdateProtocolTemplateDetailsDto updateProtocolTemplateDetailsDto,
      Principal principal) {
    log.info("User [{}] is attempted to update a Protocol Template with id [{}]",
        principal.getName(), protocolTemplateId);

    Optional<ProtocolTemplate> protocolTemplateToBeUpdated = this.protocolTemplateDataService.getProtocolTemplateById(
        protocolTemplateId);
    Optional<ProtocolTemplate> protocolTemplateByName = this.protocolTemplateDataService.findProtocolTemplateByName(
        updateProtocolTemplateDetailsDto.getName());

    if (protocolTemplateToBeUpdated.isEmpty()) {
      return generateFailureResponse("Protocol Template with id [" +
          protocolTemplateId + "] not found", HttpStatus.NOT_FOUND);
    } else if (protocolTemplateByName.isPresent() &&
        //Protocol Template Name has been updated
        // but the new Protocol Template Name already exists
        !protocolTemplateByName.get().getName()
            .equals(protocolTemplateToBeUpdated.get().getName())
    ) {
      return generateFailureResponse("Protocol Template with name [" +
          updateProtocolTemplateDetailsDto.getName() + "] already exists", HttpStatus.CONFLICT);
    } else {
      try {
        updateProtocolTemplateDetailsDto.getDefaultTriggeringStatus();
      } catch (IllegalArgumentException e) {
        return generateFailureResponse("Triggering Status with name [" +
                updateProtocolTemplateDetailsDto.getDefaultTriggeringStatusValue() + "] does not exist",
            HttpStatus.NOT_FOUND);
      }
      try {
        updateProtocolTemplateDetailsDto.getDefaultRecurrenceType();
      } catch (IllegalArgumentException e) {
        return generateFailureResponse("Recurrence Type with name [" +
                updateProtocolTemplateDetailsDto.getRecurrenceTypeValue() + "] does not exist",
            HttpStatus.NOT_FOUND);
      }
      try {
        updateProtocolTemplateDetailsDto.getTemplateCategory();
      } catch (IllegalArgumentException e) {
        return generateFailureResponse("Template Category with name [" +
                updateProtocolTemplateDetailsDto.getTemplateCategoryValue() + "] does not exist",
            HttpStatus.NOT_FOUND);
      }

      LinkedHashSet<StepTemplate> stepTemplates;

      try {
        stepTemplates = populateAssociatedProtocolStepTemplateIfExists(
            updateProtocolTemplateDetailsDto, protocolTemplateToBeUpdated.get());
      } catch (EntityNotFoundException e) {
        return generateFailureResponse(e.getMessage(), HttpStatus.NOT_FOUND);
      }

      ProtocolTemplate updatedProtocolTemplate = updateProtocolTemplate(
          updateProtocolTemplateDetailsDto,
          principal.getName(), stepTemplates, protocolTemplateToBeUpdated.get());

      protocolTemplateHelperService.removeProtocolStepsNotAssociatedWithTheUpdatedProtocolTemplate(
          updatedProtocolTemplate);
      protocolTemplateHelperService.addProtocolStepsNotAssociatedWithProtocolsMadeFromTheUpdatedProtocolTemplate(
          updatedProtocolTemplate,
          principal.getName());

      log.info("Protocol Template with ID [{}] and name [{}] updated successfully ",
          updatedProtocolTemplate.getId(), protocolTemplateToBeUpdated.get().getName());
      return ResponseEntity.status(HttpStatus.OK)
          .body("Protocol Template with ID [" + protocolTemplateId + "] and name ["
              + protocolTemplateToBeUpdated.get().getName() + "] updated successfully");
    }
  }

  private LinkedHashSet<StepTemplate> populateAssociatedProtocolStepTemplateIfExists(
      UpdateProtocolTemplateDetailsDto updateProtocolTemplateDetailsDto,
      ProtocolTemplate protocolTemplate) {
    if (updateProtocolTemplateDetailsDto.getAssociatedStepTemplateIds() != null) {
      return this.stepTemplateDataService
          .getStepTemplateEntitiesFromIdCollection(
              updateProtocolTemplateDetailsDto.getAssociatedStepTemplateIds());
    } else {
      return protocolTemplate.getProtocolTemplateSteps().stream()
          .map(ProtocolTemplateLinkedStepTemplate::getStepTemplate)
          .collect(Collectors.toCollection(LinkedHashSet::new));
    }
  }

  private ProtocolTemplate updateProtocolTemplate(
      UpdateProtocolTemplateDetailsDto updateProtocolTemplateDetailsDto,
      String modifiedBy, LinkedHashSet<StepTemplate> stepTemplates,
      ProtocolTemplate targetProtocolTemplate) {
    ProtocolTemplate updatedProtocolTemplate = ProtocolTemplateMapper.INSTANCE.toEntity(
        updateProtocolTemplateDetailsDto);

    updatedProtocolTemplate.setModifiedBy(modifiedBy);
    updatedProtocolTemplate.setProtocolTemplateSteps(
        updateProtocolStepTemplates(targetProtocolTemplate, stepTemplates,
            updatedProtocolTemplate.getStatus(), modifiedBy));

    this.protocolTemplateLinkedStepTemplateDataService
        .deleteCollectionOfProtocolTemplateLinkedStepTemplates(
            targetProtocolTemplate.getProtocolTemplateSteps());

    BeanUtility.copyPropertiesIgnoreNulls(updatedProtocolTemplate, targetProtocolTemplate);

    return this.protocolTemplateDataService.saveProtocolTemplate(targetProtocolTemplate);
  }

  private Set<ProtocolTemplateLinkedStepTemplate> updateProtocolStepTemplates(
      ProtocolTemplate protocolTemplate, LinkedHashSet<StepTemplate> stepTemplates,
      TemplateStatusEnum assignedStatus,
      String modifiedBy) {
    if (stepTemplates == null) {
      return null;
    }

    //TODO here we need to first pulled the existing entities, reorder to the provided order
    // and then re-associate those with the Protocol Template
    AtomicInteger ordinalIndex = new AtomicInteger(0);

    return stepTemplates.stream()
        .map(stepTemplate -> {
          stepTemplate.setModifiedBy(modifiedBy);
          stepTemplate.setStatus(assignedStatus);
          return stepTemplateDataService.saveStepTemplate(stepTemplate);
        })
        /*.peek(stepTemplate -> stepTemplate.getStepTemplateLinkedHomeworks().stream()
            .map(StepTemplateLinkedHomeworkTemplate::getHomeworkTemplate)
            .forEach(homeworkTemplate -> {
              homeworkTemplate.setModifiedBy(modifiedBy);
              homeworkTemplate.setStatus(assignedStatus);
              homeworkTemplateDataService.saveHomeworkTemplate(homeworkTemplate);
            }))
        .peek(stepTemplate -> stepTemplate.getStepTemplateLinkedHomeworks().stream()
            .map(StepTemplateLinkedHomeworkTemplate::getHomeworkTemplate)
            .map(HomeworkTemplate::getHomeworkQuestions)
            .flatMap(Set::stream)
            .map(HomeworkTemplateLinkedHomeworkQuestion::getHomeworkQuestion)
            .forEach(homeworkQuestion -> {
              homeworkQuestion.setModifiedBy(modifiedBy);
              homeworkQuestion.setStatus(assignedStatus);
              homeworkQuestionDataService.saveHomeworkQuestion(homeworkQuestion);
            }))*/
        .map(stepTemplate -> ProtocolTemplateLinkedStepTemplate.builder()
            .modifiedBy(modifiedBy)
            .protocolTemplate(protocolTemplate)
            .stepTemplate(stepTemplate)
            .ordinalIndex(ordinalIndex.getAndIncrement())
            .build())
        .collect(Collectors.toCollection(LinkedHashSet::new));
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
