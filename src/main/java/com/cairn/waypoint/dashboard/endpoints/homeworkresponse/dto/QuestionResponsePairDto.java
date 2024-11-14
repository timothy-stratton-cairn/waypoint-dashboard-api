package com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto;

import com.cairn.waypoint.dashboard.endpoints.homeworkquestion.SimplifiedHomeworkQuestionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponsePairDto {

  private SimplifiedHomeworkQuestionDto question;
  private SimplifiedHomeworkResponseDto response;
}
