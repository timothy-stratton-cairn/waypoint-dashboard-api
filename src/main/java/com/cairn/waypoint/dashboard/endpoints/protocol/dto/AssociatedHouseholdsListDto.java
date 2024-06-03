package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssociatedHouseholdsListDto {

  List<Long> householdIds;
  private Integer numOfHouseholds;

  public Integer getNumOfHouseholds() {
    if (householdIds == null) {
      return 0;
    }
    return householdIds.size();
  }
}
