package com.cairn.waypoint.dashboard.endpoints.householdgoals.dto;

import com.cairn.waypoint.dashboard.endpoints.homeworkquestion.dto.HomeworkQuestionDetailsDto;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class HouseholdGoalHomeworksDto {
    private Long goalId;
    private String goalName;
    private List<ProtocolWithQuestions> goalProtocols;

    @Data
    @Builder
    public static class ProtocolWithQuestions {
        private Long protocolId;
        private String protocolName;
        private List<HomeworkQuestionDetailsDto> questions;
    }
}
