package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtocolTemplateGroupedHouseholdListDto {

  private List<ProtocolTemplateGroupedHouseholdDto> households;
  private Integer numOfHouseholds;

  public Integer getNumOfAccounts() {
    return households == null ? 0 : households.size();
  }
}

