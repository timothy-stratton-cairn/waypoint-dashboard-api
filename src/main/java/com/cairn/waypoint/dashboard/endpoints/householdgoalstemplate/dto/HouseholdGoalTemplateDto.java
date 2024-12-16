package com.cairn.waypoint.dashboard.endpoints.householdgoalstemplate.dto;

import com.cairn.waypoint.dashboard.entity.GoalCategory;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HouseholdGoalTemplateDto {

  private Long id;
  private String name;
  private String description;
  private GoalCategory category;
}
