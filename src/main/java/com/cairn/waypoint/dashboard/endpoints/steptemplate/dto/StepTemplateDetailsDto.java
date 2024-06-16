package com.cairn.waypoint.dashboard.endpoints.steptemplate.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StepTemplateDetailsDto {

  private Long id;
  private String name;
  private String description;
  private String status;
  private StepTaskDetailsDto linkedStepTask;
  private HomeworkTemplateDetailsListDto linkedHomeworkTemplates;
  private StepTemplateCategoryDetailsDto category;
}
