package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import java.util.Comparator;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtocolsByProtocolTemplateListDto {

  private List<ProtocolByProtocolTemplateDto> protocols;
  private Integer numOfProtocols;

  public Integer getNumOfProtocols() {
    return protocols.size();
  }

  public List<ProtocolByProtocolTemplateDto> getProtocols() {
    Comparator<ProtocolByProtocolTemplateDto> needsAttentionComparator = Comparator.comparing(
        ProtocolByProtocolTemplateDto::getNeedsAttention).reversed();
    Comparator<ProtocolByProtocolTemplateDto> lastStatusUpdateTimestamp = Comparator.comparing(
        ProtocolByProtocolTemplateDto::getLastStatusUpdateTimestamp);

    return protocols.stream()
        .sorted(lastStatusUpdateTimestamp)
        .sorted(needsAttentionComparator)
        .toList();
  }
}
