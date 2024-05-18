package com.cairn.waypoint.dashboard.endpoints.protocolstatus.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtocolStatusListDto {

  private List<ProtocolStatusDto> statuses;
  private Integer numOfStatuses;

  public Integer getNumOfStatuses() {
    return statuses.size();
  }
}
