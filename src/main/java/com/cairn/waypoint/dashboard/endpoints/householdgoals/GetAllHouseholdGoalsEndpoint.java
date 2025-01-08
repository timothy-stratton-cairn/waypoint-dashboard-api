package com.cairn.waypoint.dashboard.endpoints.householdgoals;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.householdgoals.dto.HouseholdGoalDto;
import com.cairn.waypoint.dashboard.endpoints.householdgoals.dto.HouseholdGoalListDto;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@Tag(name = "Goals")
public class GetAllHouseholdGoalsEndpoint {

  public static final String PATH = "/api/goals";
  private final HouseholdGoalDataService dataService;
  private final HouseholdGoalMapper goalMapper;

  public GetAllHouseholdGoalsEndpoint(HouseholdGoalDataService dataService, HouseholdGoalMapper goalMapper) {
    this.dataService = dataService;
    this.goalMapper = goalMapper;
  }

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_household.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieves all Goals.",
      description = "Fetches all HouseholdGoal entities available in the system. Requires the `household.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = HouseholdGoalListDto.class))}),
          @ApiResponse(responseCode = "204", description = "No Content: No goals found",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))})
      })
  public ResponseEntity<HouseholdGoalListDto> getAllGoals() {
    log.info("Retrieving all Household Goals.");

    List<HouseholdGoal> householdGoals = dataService.getAllGoals();

    if (householdGoals.isEmpty()) {
      log.warn("No Household Goals found.");
      return ResponseEntity.noContent().build();
    }

    List<HouseholdGoalDto> goalDtos = householdGoals.stream()
        .map(goalMapper::toDto)
        .collect(Collectors.toList());

    HouseholdGoalListDto response = HouseholdGoalListDto.builder()
        .goals(goalDtos)
        .build();
    response.setTotalGoals();

    log.info("Successfully retrieved [{}] Household Goals.", goalDtos.size());
    return ResponseEntity.ok(response);
  }
}
