package com.cairn.waypoint.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdDto {

  private Long id;
  private String name;
  private HouseholdAccountListDto householdAccounts;
}
