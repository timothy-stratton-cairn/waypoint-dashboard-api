package com.cairn.waypoint.dashboard.endpoints.protocoltemplate;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplateLinkedStepTemplate;
import com.cairn.waypoint.dashboard.entity.StepTemplate;
import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolTemplateDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolTemplateLinkedStepTemplateDataService;
import com.cairn.waypoint.dashboard.service.data.StepTemplateDataService;
import com.cairn.waypoint.dashboard.service.helper.ProtocolTemplateHelperService;
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

@Slf4j
@RestController
@Tag(name = "Protocol Template")
public class RemoveStepTemplateFromProtocolTemplateByIdEndpoint {

  public static final String PATH = "/api/protocol-template/{protocolTemplateId}/step-template";
  private final ProtocolTemplateDataService protocolTemplateDataService;
  private final ProtocolTemplateLinkedStepTemplateDataService protocolTemplateLinkedStepTemplateDataService;
  private final ProtocolTemplateHelperService protocolTemplateHelperService;

  public RemoveStepTemplateFromProtocolTemplateByIdEndpoint(
      ProtocolTemplateDataService protocolTemplateDataService,
      ProtocolTemplateLinkedStepTemplateDataService protocolTemplateLinkedStepTemplateDataService,
      StepTemplateDataService stepTemplateDataService, ProtocolDataService protocolDataService
  ) {
    this.protocolTemplateDataService = protocolTemplateDataService;
    this.protocolTemplateLinkedStepTemplateDataService = protocolTemplateLinkedStepTemplateDataService;

    this.protocolTemplateHelperService = new ProtocolTemplateHelperService(protocolDataService,
        stepTemplateDataService
    );
  }

  @Transactional
  @DeleteMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.template.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Allows a user to remove one or more step template(s) from a protocol template, "
          + "removing the assigned protocol steps associated with the remove step template and any assigned homework.",
      description =
          "Allows a user to remove one or more step template(s) from a protocol template, "
              + "removing the assigned protocol steps associated with the remove step template and any assigned homework."
              + " Requires the `protocol.template.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "204",
              description = "Provided Step Template ID(s) successfully removed from Protocol Template"),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "422", description = "Conflict",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> removeStepTemplateFromProtocolTemplateById(
      @PathVariable Long protocolTemplateId,
      @RequestParam(value = "stepTemplateId") Long[] stepTemplateIds,
      Principal principal) {
    log.info(
        "[{}] is attempting to remove Step Template(s) with ID(s) [{}] from Protocol Template with ID [{}]",
        principal.getName(), stepTemplateIds, protocolTemplateId);
    Optional<ProtocolTemplate> protocolTemplateToBeUpdated = this.protocolTemplateDataService.getProtocolTemplateById(
        protocolTemplateId);

    if (protocolTemplateToBeUpdated.isEmpty()) {
      return generateFailureResponse("Protocol Template with ID [" +
          protocolTemplateId + "] not found", HttpStatus.NOT_FOUND);
    } else if (!protocolTemplateToBeUpdated.get().getProtocolTemplateSteps().stream()
        .map(ProtocolTemplateLinkedStepTemplate::getStepTemplate)
        .map(StepTemplate::getId)
        .collect(Collectors.toSet()).containsAll(Set.of(stepTemplateIds))) {
      return generateFailureResponse(
          "One or more of the provided Step Template IDs [" + Arrays.stream(stepTemplateIds)
              .map(String::valueOf).collect(Collectors.joining(","))
              + "] not associated with Protocol Template with ID [" + protocolTemplateId + "]",
          HttpStatus.UNPROCESSABLE_ENTITY);
    } else {
      Set<ProtocolTemplateLinkedStepTemplate> protocolTemplateLinkedStepTemplatesToBeDeleted = protocolTemplateToBeUpdated.get()
          .getProtocolTemplateSteps().stream()
          .filter(protocolTemplateLinkedStepTemplate -> Set.of(stepTemplateIds)
              .contains(protocolTemplateLinkedStepTemplate.getStepTemplate().getId()))
          .collect(Collectors.toSet());

      this.protocolTemplateLinkedStepTemplateDataService.deleteCollectionOfProtocolTemplateLinkedStepTemplates(
          protocolTemplateLinkedStepTemplatesToBeDeleted);

      this.protocolTemplateHelperService.removeProtocolStepsNotAssociatedWithTheUpdatedProtocolTemplate(
          this.protocolTemplateDataService.getProtocolTemplateById(
              protocolTemplateId).orElseThrow());

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
