package com.cairn.waypoint.dashboard.endpoints.steptemplate;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
//import com.cairn.waypoint.dashboard.entity.HomeworkTemplate;
import com.cairn.waypoint.dashboard.entity.ProtocolStep;
import com.cairn.waypoint.dashboard.entity.StepTemplate;
import com.cairn.waypoint.dashboard.entity.StepTemplateLinkedHomeworkTemplate;
import com.cairn.waypoint.dashboard.service.data.ProtocolStepDataService;
import com.cairn.waypoint.dashboard.service.data.StepTemplateDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/*
@Slf4j
@RestController
@Tag(name = "Protocol Step Template")
public class RemoveHomeworkTemplateFromStepTemplateByIdEndpoint {

  public static final String PATH = "/api/protocol-step-template/{stepTemplateId}/homework-template";

  private final StepTemplateDataService stepTemplateDataService;
  private final ProtocolStepDataService protocolStepDataService;

  public RemoveHomeworkTemplateFromStepTemplateByIdEndpoint(
      StepTemplateDataService stepTemplateDataService,
      ProtocolStepDataService protocolStepDataService) {
    this.stepTemplateDataService = stepTemplateDataService;
    this.protocolStepDataService = protocolStepDataService;
  }

  @Transactional
  @DeleteMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.step.template.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Allows a user to update a step template, "
          + "updating properties and the provided protocol step templates to associated step template.",
      description = "Allows a user to update a step Template, "
          + "updating properties and the provided protocol step templates to associated step template."
          + " Requires the `protocol.step.template.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "204",
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
      @RequestParam(value = "homeworkTemplateId") Long[] homeworkTemplateIds,
      Principal principal) {
    log.info(
        "User [{}] is attempting to remove Homework Template(s) with ID(s) [{}] a Step Template with ID [{}]",
        principal.getName(), homeworkTemplateIds, stepTemplateId);
    Optional<StepTemplate> stepTemplateToBeUpdated = this.stepTemplateDataService.getStepTemplateById(
        stepTemplateId);

    if (stepTemplateToBeUpdated.isEmpty()) {
      return generateFailureResponse("Step Template with ID [" +
          stepTemplateId + "] not found", HttpStatus.NOT_FOUND);
    } else if (!stepTemplateToBeUpdated.get().getStepTemplateLinkedHomeworks().stream()
        .map(StepTemplateLinkedHomeworkTemplate::getHomeworkTemplate)
        .map(HomeworkTemplate::getId)
        .collect(Collectors.toSet()).containsAll(Set.of(homeworkTemplateIds))) {
      return generateFailureResponse(
          "One or more of the provided Homework Template IDs [" + Arrays.stream(homeworkTemplateIds)
              .map(String::valueOf).collect(Collectors.joining(","))
              + "] not associated with Step Template with ID [" + stepTemplateId + "]",
          HttpStatus.UNPROCESSABLE_ENTITY);
    } else {
      stepTemplateToBeUpdated.get().getStepTemplateLinkedHomeworks().stream()
          .filter(stepTemplateLinkedHomeworkTemplate -> Set.of(homeworkTemplateIds)
              .contains(stepTemplateLinkedHomeworkTemplate.getHomeworkTemplate().getId()))
          .forEach(
              stepTemplateLinkedHomeworkTemplate -> {
                stepTemplateLinkedHomeworkTemplate.setActive(Boolean.FALSE);
                stepTemplateLinkedHomeworkTemplate.setModifiedBy(principal.getName());
              });

      this.stepTemplateDataService.saveStepTemplate(stepTemplateToBeUpdated.get());

      List<ProtocolStep> associatedProtocolSteps = this.protocolStepDataService.getProtocolStepsByStepTemplateId(
          stepTemplateId);

      associatedProtocolSteps
          .forEach(protocolStep -> {
            if (protocolStep.getLinkedHomework() != null) {
              protocolStep.getLinkedHomework().forEach(protocolStepLinkedHomework -> {
                protocolStepLinkedHomework.setActive(Boolean.FALSE);
                protocolStepLinkedHomework.setModifiedBy(principal.getName());
              });
            }
          });

      this.protocolStepDataService.saveProtocolStepList(associatedProtocolSteps);

      return ResponseEntity.noContent().build();
    }
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
*/