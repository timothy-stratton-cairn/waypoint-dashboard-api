package com.cairn.waypoint.dashboard.endpoints.dashboard.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssociatedAccountListDto {

  private List<AssociatedAccountDto> accounts;
  private Integer numOfAccounts;

  public Integer getNumOfAccounts() {
    return accounts.size();
  }

}
