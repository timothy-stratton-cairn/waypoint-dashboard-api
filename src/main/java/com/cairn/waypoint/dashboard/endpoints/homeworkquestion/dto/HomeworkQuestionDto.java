package com.cairn.waypoint.dashboard.endpoints.homeworkquestion.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomeworkQuestionDto {

  private Long questionId;
  private String questionAbbr;
  private String question;
  private String status;
  private Long categoryId;
  private Boolean active;
}
