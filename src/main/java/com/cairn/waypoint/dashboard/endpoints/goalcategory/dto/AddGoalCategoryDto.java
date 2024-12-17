package com.cairn.waypoint.dashboard.endpoints.goalcategory.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddGoalCategoryDto {
  private Long id;
  private String name;
  private String description;
}
