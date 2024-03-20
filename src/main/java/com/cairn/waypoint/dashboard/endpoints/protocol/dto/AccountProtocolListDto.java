package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import java.util.Comparator;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountProtocolListDto {

  private List<AccountProtocolDto> protocols;
  private Integer numOfProtocols;

  public Integer getNumOfProtocols() {
    return protocols.size();
  }

  public List<AccountProtocolDto> getProtocols() {
    Comparator<AccountProtocolDto> needsAttentionComparator = Comparator.comparing(
        AccountProtocolDto::getNeedsAttention).reversed();
    Comparator<AccountProtocolDto> lastStatusUpdateTimestamp = Comparator.comparing(
        AccountProtocolDto::getLastStatusUpdateTimestamp);

    return protocols.stream()
        .sorted(lastStatusUpdateTimestamp)
        .sorted(needsAttentionComparator)
        .toList();
  }
}
