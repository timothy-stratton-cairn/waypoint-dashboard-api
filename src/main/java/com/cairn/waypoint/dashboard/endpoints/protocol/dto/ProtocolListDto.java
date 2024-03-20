package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import java.util.Comparator;
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

  public List<ProtocolDto> getProtocols() {
    Comparator<ProtocolDto> needsAttentionComparator = Comparator.comparing(
        ProtocolDto::getNeedsAttention).reversed();
    Comparator<ProtocolDto> lastStatusUpdateTimestamp = Comparator.comparing(
        ProtocolDto::getLastStatusUpdateTimestamp);

    return protocols.stream()
        .sorted(lastStatusUpdateTimestamp)
        .sorted(needsAttentionComparator)
        .toList();
  }
}
