package com.cairn.waypoint.dashboard.endpoints.dashboard.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GlobalProtocolViewListDto {
  private List<GlobalProtocolViewDto> protocols;
  private Integer numOfProtocols;

  public Integer getNumOfProtocols() {
    return protocols.size();
  }
}
