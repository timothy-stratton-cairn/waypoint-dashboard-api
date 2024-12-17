package com.cairn.waypoint.dashboard.endpoints.householdgoalstemplate;

import com.cairn.waypoint.dashboard.endpoints.householdgoalstemplate.dto.AddGoalTemplateDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Tag(name = "Goal Templates")
public class CreateGoalTemplateEndpoint {

  public static final String PATH = "/api/goal-template";
  private final HouseholdGoalTemplateDataService goalTemplateService;
  private final GoalTemplateMapper goalTemplateMapper;

  public CreateGoalTemplateEndpoint(HouseholdGoalTemplateDataService goalTemplateService, GoalTemplateMapper goalTemplateMapper) {
    this.goalTemplateService = goalTemplateService;
    this.goalTemplateMapper = goalTemplateMapper;
  }

  @PostMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_goal.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Create a new Goal Template.",
      description = "Creates a new HouseholdGoalTemplate entity. Requires the `goal.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "201",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = GoalTemplate.class))}),
          @ApiResponse(responseCode = "400", description = "Invalid Request",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
      })
  public ResponseEntity<GoalTemplate> createGoalTemplate(@RequestBody AddGoalTemplateDto dto) {
    log.info("Creating a new Goal Template: {}", dto);

    GoalTemplate goalTemplate = goalTemplateMapper.toEntity(dto);
    GoalTemplate createdTemplate = goalTemplateService.createGoalTemplate(goalTemplate);

    log.info("Successfully created Goal Template with ID: {}", createdTemplate.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdTemplate);
  }
}
