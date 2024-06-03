package com.cairn.waypoint.dashboard.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdListDto {

  private List<HouseholdDto> households;
  private Integer numOfHouseholds;

  public Integer getNumOfHouseholds() {
    return households.size();
  }
}
