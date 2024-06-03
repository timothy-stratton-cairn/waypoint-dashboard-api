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
public class HouseholdAccountListDto {

  private List<HouseholdAccountDto> accounts;
  private Integer numberOfAccounts;

  public Integer getNumberOfAccounts() {
    return accounts == null || accounts.isEmpty() ? 0 : accounts.size();
  }
}
