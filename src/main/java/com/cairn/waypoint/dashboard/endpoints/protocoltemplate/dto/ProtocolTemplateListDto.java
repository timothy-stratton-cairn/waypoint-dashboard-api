package com.cairn.waypoint.dashboard.endpoints.protocoltemplate.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtocolTemplateListDto {

  private List<ProtocolTemplateDto> protocolTemplates;
  private Integer numOfProtocolTemplates;

  public Integer getNumOfProtocolTemplates() {
    return protocolTemplates.size();
  }
}
