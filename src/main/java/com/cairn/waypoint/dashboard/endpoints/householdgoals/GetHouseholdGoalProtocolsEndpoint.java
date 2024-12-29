package com.cairn.waypoint.dashboard.endpoints.householdgoals;

import com.cairn.waypoint.dashboard.endpoints.householdgoals.dto.GoalProtocolsDto;
import com.cairn.waypoint.dashboard.endpoints.householdgoals.dto.HouseholdGoalDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.AccountProtocolDto;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.HouseholdGoal;
import com.cairn.waypoint.dashboard.service.data.HouseholdGoalDataService;
import com.cairn.waypoint.dashboard.service.data.HouseholdGoalsLinkedProtocolTemplateDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
import com.cairn.waypoint.dashboard.service.helper.CustomHouseholdGoalMapper;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@RestController
@Tag(name = "Goals")
public class GetHouseholdGoalProtocolsEndpoint {

  public static final String PATH = "/api/household/goals-and-protocols/{householdId}/";

  private final HouseholdGoalDataService goalDataService;
  private final ProtocolDataService protocolDataService;
  private final HouseholdGoalMapper goalMapper;

  public GetHouseholdGoalProtocolsEndpoint(
      HouseholdGoalDataService goalDataService,
      ProtocolDataService protocolDataService,
      CustomHouseholdGoalMapper goalMapper) { // Ensure this uses your Custom Mapper
    this.goalDataService = goalDataService;
    this.protocolDataService = protocolDataService;
    this.goalMapper = goalMapper;
  }

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_household.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieve all goals and associated protocols for a given household.",
      description = "Fetches all goals linked to a household and the protocols associated with each goal.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = GoalProtocolsDto.class))}),
          @ApiResponse(responseCode = "204", description = "No Content: No data found",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(schema = @Schema(hidden = true))})
      })
  public ResponseEntity<List<GoalProtocolsDto>> getGoalsAndProtocols(@PathVariable Long householdId) {
    log.info("Fetching goals and protocols for Household ID [{}]", householdId);

    // Step 1: Retrieve household goals
    List<HouseholdGoal> householdGoalsEntity = goalDataService.getGoalsByHouseholdId(householdId);

    if (householdGoalsEntity.isEmpty()) {
      log.warn("No goals found for Household ID [{}]", householdId);
      return ResponseEntity.noContent().build();
    }

    // Use the injected mapper to convert the entity list to DTOs
    List<HouseholdGoalDto> householdGoals = goalMapper.toDtoList(householdGoalsEntity);

    // Step 2: Map goals to their associated protocols
    List<GoalProtocolsDto> goalsWithProtocols = householdGoals.stream()
        .map(goal -> {
          // Handle null or empty protocol templates
          List<Long> protocolTemplateIds = goal.getProtocolTemplates() != null
              ? goal.getProtocolTemplates().stream()
              .map(ProtocolTemplate::getId)
              .collect(Collectors.toList())
              : List.of();

          log.info("Goal ID [{}] has Protocol Template IDs: {}", goal.getId(), protocolTemplateIds);

          // Retrieve all protocols for the household
          List<Protocol> allProtocolsForHousehold = protocolDataService.getByHouseholdId(householdId);

          // Filter protocols using protocol templates
          List<AccountProtocolDto> protocols = allProtocolsForHousehold.stream()
              .filter(protocol -> protocolTemplateIds.contains(protocol.getProtocolTemplate().getId())) // Match template IDs
              .map(protocol -> AccountProtocolDto.builder()
                  .id(protocol.getId())
                  .name(protocol.getName())
                  .description(protocol.getDescription())
                  .goal(protocol.getGoal())
                  .goalProgress(protocol.getGoalProgress())
                  .createdAt(protocol.getCreated())
                  .dueBy(protocol.getDueDate())
                  .completedOn(protocol.getCompletionDate())
                  .needsAttention(protocol.getMarkedForAttention())
                  .build())
              .collect(Collectors.toList());

          // Ensure protocols is an empty list if no protocols are found
          return GoalProtocolsDto.builder()
              .goalId(goal.getId())
              .goalName(goal.getName())
              .goalDescription(goal.getDescription())
              .goalCategory(goal.getCategory() != null ? goal.getCategory().toString() : null)
              .protocols(protocols) // Protocols list will be empty if no protocols are found
              .build();
        })
        .collect(Collectors.toList());

    log.info("Successfully retrieved [{}] goals with protocols for Household ID [{}]", goalsWithProtocols.size(), householdId);

    return ResponseEntity.ok(goalsWithProtocols);
  }
}
