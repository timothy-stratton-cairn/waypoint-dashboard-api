package com.cairn.waypoint.dashboard.endpoints.steptemplate;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.UpdateStepTemplateDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.mapper.StepTemplateMapper;
import com.cairn.waypoint.dashboard.entity.StepTask;
import com.cairn.waypoint.dashboard.entity.StepTemplate;
import com.cairn.waypoint.dashboard.entity.StepTemplateCategory;
import com.cairn.waypoint.dashboard.service.data.HomeworkDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolStepDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolStepLinkedHomeworkService;
import com.cairn.waypoint.dashboard.service.data.StepTaskDataService;
import com.cairn.waypoint.dashboard.service.data.StepTemplateCategoryDataService;
import com.cairn.waypoint.dashboard.service.data.StepTemplateDataService;
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
import java.util.Optional;
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
@Tag(name = "Protocol Step Template")
public class UpdateStepTemplateEndpoint {

  public static final String PATH = "/api/protocol-step-template/{stepTemplateId}";

  private final StepTemplateDataService stepTemplateDataService;
  private final StepTaskDataService stepTaskDataService;
  //private final HomeworkTemplateDataService homeworkTemplateDataService;
  private final StepTemplateCategoryDataService stepTemplateCategoryDataService;
  private final ProtocolStepDataService protocolStepDataService;
  private final ProtocolTemplateHelperService protocolTemplateHelperService;
  private final ProtocolDataService protocolDataService;

  public UpdateStepTemplateEndpoint(StepTemplateDataService stepTemplateDataService,
      StepTaskDataService stepTaskDataService,
      //HomeworkTemplateDataService homeworkTemplateDataService,
      StepTemplateCategoryDataService stepTemplateCategoryDataService,
      ProtocolStepDataService protocolStepDataService,
      ProtocolDataService protocolDataService, HomeworkDataService homeworkDataService,
      ProtocolStepLinkedHomeworkService protocolStepLinkedHomeworkService) {
    this.stepTemplateDataService = stepTemplateDataService;
    this.stepTaskDataService = stepTaskDataService;
    this.stepTemplateCategoryDataService = stepTemplateCategoryDataService;
    this.protocolStepDataService = protocolStepDataService;
    this.protocolDataService = protocolDataService;

    protocolTemplateHelperService = new ProtocolTemplateHelperService(protocolDataService,
        stepTemplateDataService);
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
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "409", description = "Conflict",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
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
    Optional<StepTemplateCategory> stepTemplateCategory = Optional.empty();

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

    } else if (updateStepTemplateDetailsDto.getStepTemplateCategoryId() != null &&
        (stepTemplateCategory = this.stepTemplateCategoryDataService.getTemplateCategoryById(
            updateStepTemplateDetailsDto.getStepTemplateCategoryId())).isEmpty()) {
      return generateFailureResponse("Step Template Category with ID [" +
              updateStepTemplateDetailsDto.getStepTemplateCategoryId() + "] not found",
          HttpStatus.NOT_FOUND);
    } else {
      Long createdStepTemplateId = updateStepTemplate(updateStepTemplateDetailsDto,
          principal.getName(), linkedStepTask.orElse(null),
          stepTemplateCategory.orElse(null),
          stepTemplateToBeUpdated.get());

      log.info("Step Template [{}] updated successfully with ID [{}]",
          updateStepTemplateDetailsDto.getName(),
          createdStepTemplateId);
      return ResponseEntity.status(HttpStatus.OK)
          .body("Step Template with ID [" + stepTemplateId + "] and name ["
              + stepTemplateToBeUpdated.get().getName() + "] updated successfully");
    }
  }

  private Long updateStepTemplate(UpdateStepTemplateDetailsDto addStepTemplateDetailsDto,
      String modifiedBy, StepTask linkedStepTask,
      StepTemplateCategory stepTemplateCategory, StepTemplate targetStepTemplate) {

    StepTemplate stepTemplateToUpdate = StepTemplateMapper.INSTANCE
        .toEntity(addStepTemplateDetailsDto);

    stepTemplateToUpdate.setModifiedBy(modifiedBy);
    stepTemplateToUpdate.setLinkedTask(
        linkedStepTask); //TODO give the same function as linked homework
    stepTemplateToUpdate.setCategory(stepTemplateCategory);

    BeanUtility.copyPropertiesIgnoreNulls(stepTemplateToUpdate, targetStepTemplate);

    StepTemplate updatedStepTemplate = this.stepTemplateDataService.saveStepTemplate(
        targetStepTemplate);

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
