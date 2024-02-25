package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtocolListDto {

  private List<ProtocolDto> protocols;
  private Integer numOfProtocols;

  public Integer getNumOfProtocols() {
    return protocols.size();
  }
}
