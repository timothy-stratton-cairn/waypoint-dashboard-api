package com.cairn.waypoint.dashboard.endpoints.steptemplate.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddStepTemplateDetailsDto {

  private String name;
  private String description;
  private Long linkedStepTaskId;
  private List<Long> linkedHomeworkTemplateIds;
  private Long stepTemplateCategoryId;
}
