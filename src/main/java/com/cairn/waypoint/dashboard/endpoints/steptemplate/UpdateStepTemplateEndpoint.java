package com.cairn.waypoint.dashboard.endpoints.steptemplate;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.UpdateStepTemplateDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.mapper.StepTemplateMapper;
import com.cairn.waypoint.dashboard.entity.HomeworkTemplate;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolStep;
import com.cairn.waypoint.dashboard.entity.StepTask;
import com.cairn.waypoint.dashboard.entity.StepTemplate;
import com.cairn.waypoint.dashboard.entity.StepTemplateLinkedHomeworkTemplate;
import com.cairn.waypoint.dashboard.entity.TemplateCategory;
import com.cairn.waypoint.dashboard.entity.enumeration.StepStatusEnum;
import com.cairn.waypoint.dashboard.service.data.HomeworkDataService;
import com.cairn.waypoint.dashboard.service.data.HomeworkTemplateDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolStepDataService;
import com.cairn.waypoint.dashboard.service.data.StepTaskDataService;
import com.cairn.waypoint.dashboard.service.data.StepTemplateDataService;
import com.cairn.waypoint.dashboard.service.data.TemplateCategoryDataService;
import com.cairn.waypoint.dashboard.service.helper.ProtocolTemplateHelperService;
import com.cairn.waypoint.dashboard.utility.BeanUtility;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Protocol Step Template")
public class UpdateStepTemplateEndpoint {

  public static final String PATH = "/api/protocol-step-template/{stepTemplateId}";

  private final StepTemplateDataService stepTemplateDataService;
  private final StepTaskDataService stepTaskDataService;
  private final HomeworkTemplateDataService homeworkTemplateDataService;
  private final TemplateCategoryDataService templateCategoryDataService;
  private final ProtocolStepDataService protocolStepDataService;
  private final ProtocolTemplateHelperService protocolTemplateHelperService;

  public UpdateStepTemplateEndpoint(StepTemplateDataService stepTemplateDataService,
      StepTaskDataService stepTaskDataService,
      HomeworkTemplateDataService homeworkTemplateDataService,
      TemplateCategoryDataService templateCategoryDataService,
      ProtocolStepDataService protocolStepDataService,
      ProtocolDataService protocolDataService, HomeworkDataService homeworkDataService) {
    this.stepTemplateDataService = stepTemplateDataService;
    this.stepTaskDataService = stepTaskDataService;
    this.homeworkTemplateDataService = homeworkTemplateDataService;
    this.templateCategoryDataService = templateCategoryDataService;
    this.protocolStepDataService = protocolStepDataService;

    protocolTemplateHelperService = new ProtocolTemplateHelperService(protocolDataService,
        stepTemplateDataService, homeworkDataService);
  }

  @Transactional
  @PatchMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.step.template.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Allows a user to update a step template, "
          + "updating properties and the provided protocol step templates to associated step template.",
      description = "Allows a user to update a step Template, "
          + "updating properties and the provided protocol step templates to associated step template."
          + " Requires the `protocol.step.template.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              description = "Updated - Step Template update was successful"),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "409", description = "Conflict",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> updateStepTemplate(@PathVariable Long stepTemplateId,
      @RequestBody UpdateStepTemplateDetailsDto updateStepTemplateDetailsDto,
      Principal principal) {
    log.info("User [{}] is attempted to update a Step Template with ID [{}]",
        principal.getName(), stepTemplateId);

    Optional<StepTemplate> stepTemplateToBeUpdated = this.stepTemplateDataService.getStepTemplateById(
        stepTemplateId);
    Optional<StepTemplate> stepTemplateByName = this.stepTemplateDataService.findStepTemplateByName(
        updateStepTemplateDetailsDto.getName());

    Optional<StepTask> linkedStepTask = Optional.empty();
    List<HomeworkTemplate> linkedHomeworkTemplates = new ArrayList<>();
    Optional<TemplateCategory> stepTemplateCategory = Optional.empty();

    if (stepTemplateToBeUpdated.isEmpty()) {
      return generateFailureResponse("Step Template with ID [" +
          stepTemplateId + "] not found", HttpStatus.NOT_FOUND);
    } else if (stepTemplateByName.isPresent() &&
        //Step Template Name has been updated
        // but the new Step Template Name already exists
        !stepTemplateByName.get().getName()
            .equals(stepTemplateToBeUpdated.get().getName())
    ) {
      return generateFailureResponse("Step Template with name [" +
          updateStepTemplateDetailsDto.getName() + "] already exists", HttpStatus.CONFLICT);
    } else if (updateStepTemplateDetailsDto.getLinkedStepTaskId() != null &&
        (linkedStepTask = this.stepTaskDataService.getStepTaskById(
            updateStepTemplateDetailsDto.getLinkedStepTaskId()))
            .isEmpty()) {
      return generateFailureResponse("Step Task with ID [" +
          updateStepTemplateDetailsDto.getLinkedStepTaskId() + "] not found", HttpStatus.NOT_FOUND);
    } else if (updateStepTemplateDetailsDto.getLinkedHomeworkTemplateIds() != null &&
        !updateStepTemplateDetailsDto.getLinkedHomeworkTemplateIds().isEmpty() &&
        (linkedHomeworkTemplates = this.homeworkTemplateDataService.getHomeworkTemplates(
            updateStepTemplateDetailsDto.getLinkedHomeworkTemplateIds())).isEmpty()) {
      return generateFailureResponse("Homework Templates with ID [" +
              updateStepTemplateDetailsDto.getLinkedHomeworkTemplateIds() + "] not found",
          HttpStatus.NOT_FOUND);
    } else if (updateStepTemplateDetailsDto.getStepTemplateCategoryId() != null &&
        (stepTemplateCategory = this.templateCategoryDataService.getTemplateCategoryById(
            updateStepTemplateDetailsDto.getStepTemplateCategoryId())).isEmpty()) {
      return generateFailureResponse("Step Template Category with ID [" +
              updateStepTemplateDetailsDto.getStepTemplateCategoryId() + "] not found",
          HttpStatus.NOT_FOUND);
    } else {
      Long createdStepTemplateId = updateStepTemplate(updateStepTemplateDetailsDto,
          principal.getName(), linkedStepTask.orElse(null),
          linkedHomeworkTemplates, stepTemplateCategory.orElse(null),
          stepTemplateToBeUpdated.get());

      log.info("Step Task [{}] updated successfully with ID [{}]",
          updateStepTemplateDetailsDto.getName(),
          createdStepTemplateId);
      return ResponseEntity.status(HttpStatus.OK)
          .body("Step Template with ID [" + stepTemplateId + "] and name ["
              + stepTemplateToBeUpdated.get().getName() + "] updated successfully");
    }
  }

  private Long updateStepTemplate(UpdateStepTemplateDetailsDto addStepTemplateDetailsDto,
      String modifiedBy, StepTask linkedStepTask, List<HomeworkTemplate> linkedHomeworkTemplates,
      TemplateCategory templateCategory, StepTemplate targetStepTemplate) {

    StepTemplate stepTemplateToUpdate = StepTemplateMapper.INSTANCE
        .toEntity(addStepTemplateDetailsDto);

    stepTemplateToUpdate.setModifiedBy(modifiedBy);
    stepTemplateToUpdate.setLinkedTask(
        linkedStepTask); //TODO give the same function as linked homework
    stepTemplateToUpdate.setCategory(templateCategory);

    BeanUtility.copyPropertiesIgnoreNulls(stepTemplateToUpdate, targetStepTemplate);

    StepTemplate updatedStepTemplate = this.stepTemplateDataService.saveStepTemplate(
        targetStepTemplate);

    if (!linkedHomeworkTemplates.isEmpty()) {
      updatedStepTemplate.setStepTemplateLinkedHomeworks(linkedHomeworkTemplates.stream()
          .map(homeworkTemplate -> StepTemplateLinkedHomeworkTemplate.builder()
              .homeworkTemplate(homeworkTemplate)
              .stepTemplate(updatedStepTemplate)
              .build())
          .collect(Collectors.toSet()));

      this.stepTemplateDataService.saveStepTemplate(updatedStepTemplate);

      List<ProtocolStep> protocolStepsToUpdate = this.protocolStepDataService.getProtocolStepsByStepTemplateId(
          targetStepTemplate.getId());

      Consumer<Protocol> assignHomeworkToUpdatedHomeworkSteps = (protocolWhoseStepsNeedUpdating) ->
          this.protocolTemplateHelperService.generateAndSaveClientHomework(
              protocolWhoseStepsNeedUpdating, modifiedBy);

      protocolStepsToUpdate.stream()
          .map(ProtocolStep::getParentProtocol)
          .peek(assignHomeworkToUpdatedHomeworkSteps);

      protocolStepsToUpdate.stream()
          .filter(protocolStep -> protocolStep.getStatus().equals(StepStatusEnum.DONE))
          .peek(protocolStep -> protocolStep.setStatus(StepStatusEnum.IN_PROGRESS))
          .forEach(this.protocolStepDataService::saveProtocolStep);
    }
    return updatedStepTemplate.getId();
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
