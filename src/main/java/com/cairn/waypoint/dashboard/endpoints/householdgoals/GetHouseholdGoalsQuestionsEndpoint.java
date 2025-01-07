package com.cairn.waypoint.dashboard.endpoints.householdgoals;

import com.cairn.waypoint.dashboard.endpoints.householdgoals.dto.HouseholdGoalHomeworksDto;
import com.cairn.waypoint.dashboard.endpoints.homeworkquestion.dto.HomeworkQuestionDetailsDto;
import com.cairn.waypoint.dashboard.entity.HouseholdGoal;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.service.data.HouseholdGoalDataService;
import com.cairn.waypoint.dashboard.service.data.HomeworkQuestionDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
import com.cairn.waypoint.dashboard.service.helper.CustomHouseholdGoalMapper;
import com.cairn.waypoint.dashboard.service.helper.HomeworkQuestionHelperService;
import com.cairn.waypoint.dashboard.service.helper.QuestionMapper;
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
@Tag(name = "Household Goals Questions")
public class GetHouseholdGoalsQuestionsEndpoint {

    public static final String PATH = "/api/household/goals-questions/{householdId}";

    private final HouseholdGoalDataService goalDataService;
    private final ProtocolDataService protocolDataService;
    private final HomeworkQuestionDataService homeworkQuestionDataService;
    private final HomeworkQuestionHelperService homeworkQuestionHelperService;
    private final CustomHouseholdGoalMapper goalMapper;

    public GetHouseholdGoalsQuestionsEndpoint(
            HouseholdGoalDataService goalDataService,
            ProtocolDataService protocolDataService,
            HomeworkQuestionDataService homeworkQuestionDataService,
            HomeworkQuestionHelperService homeworkQuestionHelperService,
            CustomHouseholdGoalMapper goalMapper) {
        this.goalDataService = goalDataService;
        this.protocolDataService = protocolDataService;
        this.homeworkQuestionDataService = homeworkQuestionDataService;
        this.homeworkQuestionHelperService = homeworkQuestionHelperService;
        this.goalMapper = goalMapper;
    }

    @GetMapping(PATH)
    @PreAuthorize("hasAnyAuthority('SCOPE_household.full', 'SCOPE_admin.full')")
    @Operation(
            summary = "Retrieve household goals, protocols, and associated questions.",
            description = "Fetches household goals, protocols linked to those goals, and questions assigned to the protocols.",
            security = @SecurityRequirement(name = "oAuth2JwtBearer"),
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = HouseholdGoalHomeworksDto.class))}),
                    @ApiResponse(responseCode = "204", description = "No Content: No data found",
                            content = {@Content(schema = @Schema(hidden = true))}),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = {@Content(schema = @Schema(hidden = true))}),
                    @ApiResponse(responseCode = "403", description = "Forbidden",
                            content = {@Content(schema = @Schema(hidden = true))})
            })
    public ResponseEntity<List<HouseholdGoalHomeworksDto>> getHouseholdGoalsQuestions(@PathVariable Long householdId) {
        log.info("Fetching household goals, protocols, and questions for Household ID [{}]", householdId);

        // Fetch all household goals
        List<HouseholdGoal> householdGoals = goalDataService.getGoalsByHouseholdId(householdId);
        if (householdGoals.isEmpty()) {
            log.warn("No goals found for Household ID [{}]", householdId);
            return ResponseEntity.noContent().build();
        }

        // Map household goals to DTOs
        List<HouseholdGoalHomeworksDto> result = householdGoals.stream()
                .map(goal -> {
                    // Fetch protocols for the household and filter those linked to the goal
                    List<Protocol> protocols = protocolDataService.getByHouseholdId(householdId)
                            .stream()
                            .filter(protocol -> protocol.getGoal().equals(goal.getId()))
                            .collect(Collectors.toList());

                    // Map protocols and their questions
                    List<HouseholdGoalHomeworksDto.ProtocolWithQuestions> protocolsWithQuestions = protocols.stream()
                            .map(protocol -> HouseholdGoalHomeworksDto.ProtocolWithQuestions.builder()
                                    .protocolId(protocol.getId())
                                    .protocolName(protocol.getName())
                                    .questions(
                                            homeworkQuestionDataService.getHomeworkQuestionsByProtocolId(protocol.getId())
                                                    .stream()
                                                    .map(QuestionMapper.INSTANCE::toDetailsDto) // Use the mapper here
                                                    .collect(Collectors.toList())
                                    )
                                    .build())
                            .collect(Collectors.toList());

                    return HouseholdGoalHomeworksDto.builder()
                            .goalId(goal.getId())
                            .goalName(goal.getName())
                            .goalProtocols(protocolsWithQuestions)
                            .build();
                })
                .collect(Collectors.toList());

        log.info("Successfully retrieved goals, protocols, and questions for Household ID [{}]", householdId);
        return ResponseEntity.ok(result);
    }
}
