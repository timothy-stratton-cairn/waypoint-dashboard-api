package com.cairn.waypoint.dashboard.endpoints.steptemplate.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StepTemplateDto {

  private Long id;
  private String name;
  private String description;
  private HomeworkTemplateDetailsListDto linkedHomeworkTemplates;
  private StepTemplateCategoryDetailsDto category;
}
