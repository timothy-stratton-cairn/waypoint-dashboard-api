package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import java.util.Comparator;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpcomingProtocolListDto {

  private List<UpcomingProtocolDto> protocols;
  private Integer numOfProtocols;

  public Integer getNumOfProtocols() {
    return protocols.size();
  }

  public List<UpcomingProtocolDto> getProtocols() {
    Comparator<UpcomingProtocolDto> willReoccurOnComparator = Comparator.comparing(
        UpcomingProtocolDto::getWillReoccurOn).reversed();

    return protocols.stream()
        .sorted(willReoccurOnComparator)
        .toList();
  }
}
