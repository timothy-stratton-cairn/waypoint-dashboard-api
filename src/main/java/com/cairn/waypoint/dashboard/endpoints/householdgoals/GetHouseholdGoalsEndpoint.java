package com.cairn.waypoint.dashboard.endpoints.householdgoals;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.entity.HouseholdGoal;
import com.cairn.waypoint.dashboard.service.data.HouseholdGoalDataService;
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

@Slf4j
@RestController
@Tag(name = "Goals")
public class GetHouseholdGoalsEndpoint {

  public static final String PATH = "/api/household/{householdId}/goals";
  private final HouseholdGoalDataService dataService;

  public GetHouseholdGoalsEndpoint(HouseholdGoalDataService dataService) {
    this.dataService = dataService;
  }

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_household.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieves all Household Goals linked to a given Household ID.",
      description = "Fetches all HouseholdGoal entities for the provided Household ID. Requires the `household.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = HouseholdGoal.class))}),
          @ApiResponse(responseCode = "204", description = "No Content: No goals found",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))})
      })
  public ResponseEntity<?> getHouseholdGoalsByHouseholdId(@PathVariable Long householdId) {
    log.info("Retrieving Household Goals for Household ID [{}]", householdId);

    List<HouseholdGoal> householdGoals = dataService.getGoalsByHouseholdId(householdId);

    if (householdGoals.isEmpty()) {
      log.warn("No Household Goals found for Household ID [{}]", householdId);
      return ResponseEntity.noContent().build();
    }
    log.info("Successfully retrieved [{}] Household Goals for Household ID [{}]", householdGoals.size(), householdId);
    return ResponseEntity.ok(householdGoals);
  }
}
