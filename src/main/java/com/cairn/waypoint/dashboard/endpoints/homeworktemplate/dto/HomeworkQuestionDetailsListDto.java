package com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomeworkQuestionDetailsListDto {

  private List<HomeworkQuestionDetailsDto> questions;
  private Integer numOfQuestions;

  public Integer getNumOfQuestions() {
    return questions.size();
  }
}
