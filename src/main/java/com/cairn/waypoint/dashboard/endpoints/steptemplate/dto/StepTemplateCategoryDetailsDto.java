package com.cairn.waypoint.dashboard.endpoints.steptemplate.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StepTemplateCategoryDetailsDto {
  private Long id;
  private String name;
  private String description;
}
