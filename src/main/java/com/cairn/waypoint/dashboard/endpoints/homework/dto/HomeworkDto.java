package com.cairn.waypoint.dashboard.endpoints.homework.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomeworkDto {

  private Long homeworkId;
  private String name;
  private String description;
  private HomeworkQuestionListDto homeworkQuestions;
  private Long assignedHouseholdId;
  private Long parentProtocolId;
  private Long parentProtocolStepId;
  private Long categoryId;
  private Boolean active;
}
