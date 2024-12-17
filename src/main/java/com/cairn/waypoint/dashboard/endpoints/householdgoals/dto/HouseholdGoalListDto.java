package com.cairn.waypoint.dashboard.endpoints.householdgoals.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HouseholdGoalListDto {

  private List<HouseholdGoalDto> goals;
  private Integer totalGoals;

  public void setTotalGoals() {
    this.totalGoals = goals.size();
  }
}
