package com.cairn.waypoint.dashboard.endpoints.householdgoalstemplate;

import com.amazonaws.services.sqs.model.ResourceNotFoundException;
import com.cairn.waypoint.dashboard.endpoints.householdgoalstemplate.dto.HouseholdGoalTemplateDto;
import com.cairn.waypoint.dashboard.entity.GoalTemplate;
import com.cairn.waypoint.dashboard.service.data.HouseholdGoalTemplateDataService;
import com.cairn.waypoint.dashboard.service.helper.GoalTemplateMapper;
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

@RestController
@Slf4j
@RequestMapping(GetHouseholdGoalTemplateByIdEndpoint.PATH)
@Tag(name = "Goal Templates")
public class GetHouseholdGoalTemplateByIdEndpoint {

  public static final String PATH = "/api/goal-templates/{templateId}";

  private final HouseholdGoalTemplateDataService goalTemplateService;
  private final GoalTemplateMapper goalTemplateMapper;

  public GetHouseholdGoalTemplateByIdEndpoint(HouseholdGoalTemplateDataService goalTemplateService, GoalTemplateMapper goalTemplateMapper) {
    this.goalTemplateService = goalTemplateService;
    this.goalTemplateMapper = goalTemplateMapper;
  }

  @GetMapping
  @PreAuthorize("hasAnyAuthority('SCOPE_goal.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Get a single Household Goal Template by ID.",
      description = "Retrieves a specific HouseholdGoalTemplate by its templateId.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = HouseholdGoalTemplateDto.class))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
      })
  public ResponseEntity<HouseholdGoalTemplateDto> getGoalTemplateById(@PathVariable Long templateId) {
    log.info("Fetching Household Goal Template with ID {}", templateId);

    GoalTemplate goalTemplate = goalTemplateService.getGoalTemplateById(templateId)
        .orElseThrow(() -> new ResourceNotFoundException("GoalTemplate not found with ID: " + templateId));

    HouseholdGoalTemplateDto dto = goalTemplateMapper.toDto(goalTemplate);

    log.info("Successfully retrieved Household Goal Template with ID {}", templateId);
    return ResponseEntity.ok(dto);
  }
}
