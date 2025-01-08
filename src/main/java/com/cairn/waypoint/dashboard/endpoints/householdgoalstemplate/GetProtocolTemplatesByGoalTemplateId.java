package com.cairn.waypoint.dashboard.endpoints.householdgoalstemplate;

import com.cairn.waypoint.dashboard.endpoints.protocoltemplate.dto.ProtocolTemplateDto;
import com.cairn.waypoint.dashboard.entity.HouseholdLinkedProtocolGoalTemplates;
import com.cairn.waypoint.dashboard.service.data.HouseholdGoalsLinkedProtocolTemplateDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping(GetProtocolTemplatesByGoalTemplateId.PATH)
@Tag(name = "Goal Templates")
public class GetProtocolTemplatesByGoalTemplateId {

  public static final String PATH = "/api/goal-templates/linked-protocols/{goalTemplateId}";

  private final HouseholdGoalsLinkedProtocolTemplateDataService linkedTemplateService;

  public GetProtocolTemplatesByGoalTemplateId(HouseholdGoalsLinkedProtocolTemplateDataService linkedTemplateService) {
    this.linkedTemplateService = linkedTemplateService;
  }

  @GetMapping
  @PreAuthorize("hasAnyAuthority('SCOPE_goal.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Get Protocol Templates linked to a Goal Template.",
      description = "Retrieves all protocol templates linked to a specific goal template.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ProtocolTemplateDto.class))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
      })
  public ResponseEntity<List<ProtocolTemplateDto>> getLinkedProtocolsForGoalTemplate(@PathVariable Long goalTemplateId) {
    log.info("Fetching linked Protocol Templates for Goal Template with ID {}", goalTemplateId);

    List<HouseholdLinkedProtocolGoalTemplates> linkedTemplates =
        linkedTemplateService.getAllHouseholdLinkedProtocolGoalTemplates();

    // Convert to DTOs
    List<ProtocolTemplateDto> protocolDtos = linkedTemplates.stream()
        .filter(link -> link.getGoalTemplate().getId().equals(goalTemplateId))
        .map(link -> {
          var protocolTemplate = link.getProtocolTemplate();
          return ProtocolTemplateDto.builder()
              .id(protocolTemplate.getId())
              .name(protocolTemplate.getName())
              .description(protocolTemplate.getDescription())
              .build();
        })
        .collect(Collectors.toList());

    log.info("Successfully retrieved {} linked Protocol Templates for Goal Template with ID {}.",
        protocolDtos.size(), goalTemplateId);

    return ResponseEntity.ok(protocolDtos);
  }
}
