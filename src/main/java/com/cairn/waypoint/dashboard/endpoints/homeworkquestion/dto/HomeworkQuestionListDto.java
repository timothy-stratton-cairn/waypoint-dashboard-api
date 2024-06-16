package com.cairn.waypoint.dashboard.endpoints.homeworkquestion.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomeworkQuestionListDto {

  private List<HomeworkQuestionDto> questions;
  private Integer numOfQuestions;

  public Integer getNumOfQuestions() {
    return questions.size();
  }
}
