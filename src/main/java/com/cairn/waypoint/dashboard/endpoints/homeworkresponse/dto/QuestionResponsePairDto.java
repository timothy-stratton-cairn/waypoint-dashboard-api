package com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto;

import com.cairn.waypoint.dashboard.endpoints.homeworkquestion.SimplifiedHomeworkQuestionDto;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.HomeworkResponse;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponsePairDto {
    private SimplifiedHomeworkQuestionDto question;
    private SimplifiedHomeworkResponseDto response;
}
