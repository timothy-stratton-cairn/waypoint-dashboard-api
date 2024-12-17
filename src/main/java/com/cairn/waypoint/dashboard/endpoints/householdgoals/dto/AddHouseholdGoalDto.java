package com.cairn.waypoint.dashboard.endpoints.householdgoals.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddHouseholdGoalDto {
  private String name;
  private String description;
  private Long goalTemplateId;
  private Long householdId;
}