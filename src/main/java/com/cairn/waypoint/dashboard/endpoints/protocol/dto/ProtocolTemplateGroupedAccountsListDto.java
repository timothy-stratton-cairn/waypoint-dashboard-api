package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import com.cairn.waypoint.dashboard.dto.AccountDto;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtocolTemplateGroupedAccountsListDto {

  private List<ProtocolTemplateGroupedAccountDto> accounts;
  private Integer numOfAccounts;

  public Integer getNumOfAccounts() {
    return accounts.size();
  }
}

