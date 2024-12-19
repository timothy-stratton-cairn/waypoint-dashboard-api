package com.cairn.waypoint.dashboard.endpoints.householdgoals.dto;

import com.cairn.waypoint.dashboard.entity.GoalCategory;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HouseholdGoalDto {

  private Long id;
  private String name;
  private String description;
  private GoalCategory category;
  private List<ProtocolTemplate> protocolTemplates;

}
