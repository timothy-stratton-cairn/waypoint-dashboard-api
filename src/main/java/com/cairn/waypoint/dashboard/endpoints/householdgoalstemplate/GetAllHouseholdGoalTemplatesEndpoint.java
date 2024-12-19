package com.cairn.waypoint.dashboard.endpoints.householdgoalstemplate;

import com.cairn.waypoint.dashboard.endpoints.householdgoalstemplate.dto.HouseholdGoalTemplateDto;
import com.cairn.waypoint.dashboard.endpoints.householdgoalstemplate.dto.HouseholdGoalTemplateListDto;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping(CreateGoalTemplateEndpoint.PATH)
@Tag(name = "Goal Templates")
public class GetAllHouseholdGoalTemplatesEndpoint {

  private final HouseholdGoalTemplateDataService goalTemplateService;
  private final GoalTemplateMapper goalTemplateMapper;

  public GetAllHouseholdGoalTemplatesEndpoint(HouseholdGoalTemplateDataService goalTemplateService, GoalTemplateMapper goalTemplateMapper) {
    this.goalTemplateService = goalTemplateService;
    this.goalTemplateMapper = goalTemplateMapper;
  }

  @GetMapping
  @PreAuthorize("hasAnyAuthority('SCOPE_goal.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Get all Household Goal Templates.",
      description = "Retrieves a list of all HouseholdGoalTemplates with their details.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = HouseholdGoalTemplateListDto.class))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
      })
  public ResponseEntity<HouseholdGoalTemplateListDto> getAllGoalTemplates() {
    log.info("Fetching all Household Goal Templates.");

    List<GoalTemplate> goalTemplates = goalTemplateService.getAllGoalTemplates();

    List<HouseholdGoalTemplateDto> dtos = goalTemplates.stream()
        .map(goalTemplateMapper::toDto)
        .collect(Collectors.toList());

    HouseholdGoalTemplateListDto response = HouseholdGoalTemplateListDto.builder()
        .templates(dtos)
        .build();
    response.setTotalTemplate(dtos.size());

    log.info("Successfully retrieved {} Household Goal Templates.", dtos.size());
    return ResponseEntity.ok(response);
  }
}
