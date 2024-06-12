package com.cairn.waypoint.dashboard.endpoints.steptemplate;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.entity.ProtocolStep;
import com.cairn.waypoint.dashboard.entity.StepTemplate;
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
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Protocol Step Template")
public class DeleteStepTemplateEndpoint {

  public static final String PATH = "/api/protocol-step-template/{stepTemplateId}";

  private final StepTemplateDataService stepTemplateDataService;
  private final ProtocolStepDataService protocolStepDataService;

  public DeleteStepTemplateEndpoint(StepTemplateDataService stepTemplateDataService,
      ProtocolStepDataService protocolStepDataService) {
    this.stepTemplateDataService = stepTemplateDataService;
    this.protocolStepDataService = protocolStepDataService;
  }

  @Transactional
  @DeleteMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.step.template.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Allows a user to deactivate a step template.",
      description = "Allows a user to deactivate a step Template."
          + " Requires the `protocol.step.template.full` permission.",
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
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "409", description = "Conflict",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> updateStepTemplate(@PathVariable Long stepTemplateId,
      Principal principal) {
    log.info("User [{}] is attempting to deactivate a Step Template with ID [{}]",
        principal.getName(), stepTemplateId);

    final ResponseEntity<?>[] response = new ResponseEntity<?>[1];
    this.stepTemplateDataService.getStepTemplateById(stepTemplateId)
        .ifPresentOrElse(
            returnedStepTemplate -> response[0] = generateSuccessResponse(returnedStepTemplate,
                principal.getName()),
            () -> response[0] = generateFailureResponse(stepTemplateId)
        );

    return response[0];
  }

  public ResponseEntity<String> generateSuccessResponse(
      StepTemplate stepTemplateToDelete, String modifiedBy) {
    List<ProtocolStep> protocolStepList = protocolStepDataService.getProtocolStepsByStepTemplateId(
        stepTemplateToDelete.getId());

    protocolStepList.forEach(protocolStep -> {
      protocolStep.getNotes().forEach(note -> {
        note.setActive(Boolean.FALSE);
        note.setModifiedBy(modifiedBy);
      });
      protocolStep.getLinkedHomework().forEach(homeworkLink -> {
        homeworkLink.setActive(Boolean.FALSE);
        homeworkLink.setModifiedBy(modifiedBy);
      });

      protocolStep.setActive(Boolean.FALSE);
      protocolStep.setModifiedBy(modifiedBy);

      protocolStepDataService.saveProtocolStep(protocolStep);
    });

    stepTemplateToDelete.setActive(Boolean.FALSE);
    stepTemplateToDelete.setModifiedBy(modifiedBy);

    stepTemplateToDelete.getStepTemplateLinkedHomeworks().forEach(linkedHomeworkTemplate -> {
      linkedHomeworkTemplate.setActive(Boolean.FALSE);
      linkedHomeworkTemplate.setModifiedBy(modifiedBy);
    });

    stepTemplateDataService.saveStepTemplate(stepTemplateToDelete);

    log.info("Step Template with ID [{}] has been deactivated", stepTemplateToDelete.getId());
    return ResponseEntity.ok(
        "Step Template with ID [" + stepTemplateToDelete.getId() + "] has been deactivated");
  }

  public ResponseEntity<ErrorMessage> generateFailureResponse(Long stepTemplateId) {
    log.warn("Step Template with ID [{}] not found", stepTemplateId);
    return new ResponseEntity<>(
        ErrorMessage.builder()
            .path(PATH)
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Step Template with ID [" + stepTemplateId + "] not found")
            .build(),
        HttpStatus.NOT_FOUND
    );
  }
}
