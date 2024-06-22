package com.cairn.waypoint.dashboard.dto.authorization;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountListDto {

  private List<AccountDto> accounts;
  private Integer numOfAccounts;

  public Integer getNumOfAccounts() {
    return accounts.size();
  }
}

