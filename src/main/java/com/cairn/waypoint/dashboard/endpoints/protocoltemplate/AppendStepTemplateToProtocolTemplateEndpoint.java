package com.cairn.waypoint.dashboard.endpoints.protocoltemplate;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplateLinkedStepTemplate;
import com.cairn.waypoint.dashboard.entity.StepTemplate;
import com.cairn.waypoint.dashboard.service.ProtocolTemplateService;
import com.cairn.waypoint.dashboard.service.StepTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Protocol Template")
public class AppendStepTemplateToProtocolTemplateEndpoint {

  public static final String PATH = "/api/protocol-template/{protocolTemplateId}/{stepTemplateId}";

  private final ProtocolTemplateService protocolTemplateService;
  private final StepTemplateService stepTemplateService;

  public AppendStepTemplateToProtocolTemplateEndpoint(
      ProtocolTemplateService protocolTemplateService,
      StepTemplateService stepTemplateService) {
    this.protocolTemplateService = protocolTemplateService;
    this.stepTemplateService = stepTemplateService;
  }

  @PatchMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.template.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Allows a user to add a step template to a protocol template,"
          + " keeping all other details the same.",
      description = "Allows a user to add a step template to a protocol template, "
          + "keeping all other details the same."
          + " Requires the `protocol.template.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              description = "Updated - Protocol Template update was successful",
              content = {@Content(schema = @Schema(implementation = String.class))}),
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
  public ResponseEntity<?> updateProtocolTemplate(@PathVariable Long protocolTemplateId,
      @PathVariable Long stepTemplateId, Principal principal) {
    log.info(
        "User [{}] is attempting to update a Protocol Template with id [{}] by appending Step Template with ID [{}]",
        principal.getName(), protocolTemplateId, stepTemplateId);

    Optional<ProtocolTemplate> protocolTemplateToBeUpdated = this.protocolTemplateService.getProtocolTemplateById(
        protocolTemplateId);
    Optional<StepTemplate> stepTemplateToBeAppended = this.stepTemplateService.getStepTemplateById(
        stepTemplateId);

    if (protocolTemplateToBeUpdated.isEmpty()) {
      return generateFailureResponse("Protocol Template with id [" +
          protocolTemplateId + "] not found", HttpStatus.NOT_FOUND);
    } else if (stepTemplateToBeAppended.isEmpty()) {
      return generateFailureResponse("Step Template with id [" +
          stepTemplateId + "] not found", HttpStatus.NOT_FOUND);
    } else {
      ProtocolTemplate protocolTemplate = protocolTemplateToBeUpdated.get();
      StepTemplate stepTemplate = stepTemplateToBeAppended.get();

      ProtocolTemplateLinkedStepTemplate protocolTemplateLinkedStepTemplate = ProtocolTemplateLinkedStepTemplate.builder()
          .protocolTemplate(protocolTemplate)
          .stepTemplate(stepTemplate)
          .ordinalIndex(protocolTemplate.getProtocolTemplateSteps().size())
          .build();

      protocolTemplate.getProtocolTemplateSteps().add(protocolTemplateLinkedStepTemplate);

      Long updatedProtocolTemplateId = protocolTemplateService.saveProtocolTemplate(
          protocolTemplate);

      log.info(
          "Protocol Template with ID [{}] and name [{}] updated successfully with Step Template [{}] ",
          updatedProtocolTemplateId, protocolTemplateToBeUpdated.get().getName(),
          stepTemplate.getId());
      return ResponseEntity.status(HttpStatus.OK)
          .body("Protocol Template with ID [" + protocolTemplateId + "] and name ["
              + protocolTemplateToBeUpdated.get().getName() + "] updated successfully");
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
