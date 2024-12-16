package com.cairn.waypoint.dashboard.endpoints.householdgoals;

import com.cairn.waypoint.dashboard.endpoints.householdgoals.dto.AddHouseholdGoalDto;
import com.cairn.waypoint.dashboard.entity.HouseholdGoal;
import com.cairn.waypoint.dashboard.service.data.HouseholdGoalDataService;
import com.cairn.waypoint.dashboard.service.helper.HouseholdGoalMapper;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Tag(name = "Goals")
public class CreateHouseholdGoalEndpoint {

  public static final String PATH = "/api/goal";
  private final HouseholdGoalDataService goalDataService;
  private final HouseholdGoalMapper goalMapper;

  public CreateHouseholdGoalEndpoint(HouseholdGoalDataService goalDataService, HouseholdGoalMapper goalMapper) {
    this.goalDataService = goalDataService;
    this.goalMapper = goalMapper;
  }

  @PostMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_goal.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Create a new Goal.",
      description = "Creates a new HouseholdGoal entity. Requires the `goal.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "201",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = HouseholdGoal.class))}),
          @ApiResponse(responseCode = "400", description = "Invalid Request",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
      })
  public ResponseEntity<HouseholdGoal> createGoal(@RequestBody AddHouseholdGoalDto dto) {
    log.info("Creating a new Household Goal: {}", dto);

    HouseholdGoal householdGoal = goalMapper.toEntity(dto);
    HouseholdGoal createdGoal = goalDataService.createGoal(householdGoal);

    log.info("Successfully created Household Goal with ID: {}", createdGoal.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdGoal);
  }
}