package com.cairn.waypoint.dashboard.endpoints.householdgoalstemplate.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddGoalTemplateDto {
  private String name;
  private String description;
  private Long categoryId;
}