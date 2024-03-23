package com.cairn.waypoint.dashboard.endpoints.steptemplatecategory.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChildTemplateCategoryDto {

  private Long id;
  private String name;
  private String description;
}
