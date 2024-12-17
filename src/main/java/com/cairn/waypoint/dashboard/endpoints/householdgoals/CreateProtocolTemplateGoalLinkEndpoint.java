package com.cairn.waypoint.dashboard.endpoints.householdgoals;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.entity.HouseholdLinkedProtocolGoalTemplates;
import com.cairn.waypoint.dashboard.service.data.HouseholdGoalsLinkedProtocolTemplateDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Goals")
public class CreateProtocolTemplateGoalLinkEndpoint {

  public static final String PATH = "/api/goals/link";
  private final HouseholdGoalsLinkedProtocolTemplateDataService linkedDataService;

  @Value("${waypoint.dashboard.base-url}")
  private String baseUrl;

  public CreateProtocolTemplateGoalLinkEndpoint(HouseholdGoalsLinkedProtocolTemplateDataService linkedDataService) {
    this.linkedDataService = linkedDataService;
  }

  @PostMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Creates a link between a ProtocolTemplate and a GoalTemplate.",
      description = "Creates a link between a ProtocolTemplate and a GoalTemplate if one does not already exist. Requires the `protocol.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = HouseholdLinkedProtocolGoalTemplates.class))}),
          @ApiResponse(responseCode = "409", description = "Conflict: Link already exists",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))}),
      })
  public ResponseEntity<?> createProtocolGoalLink(
      @RequestParam Long protocolTemplateId,
      @RequestParam Long goalTemplateId) {

    log.info("Creating link between ProtocolTemplate [{}] and GoalTemplate [{}]", protocolTemplateId, goalTemplateId);

    if (linkedDataService.linkExists(protocolTemplateId, goalTemplateId)) {
      log.warn("Link between ProtocolTemplate [{}] and GoalTemplate [{}] already exists", protocolTemplateId, goalTemplateId);
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Link already exists");
    }

    HouseholdLinkedProtocolGoalTemplates newLink =
        linkedDataService.createLink(protocolTemplateId, goalTemplateId);

    log.info("Successfully created link: {}", newLink);
    return ResponseEntity.ok(newLink);
  }
}
