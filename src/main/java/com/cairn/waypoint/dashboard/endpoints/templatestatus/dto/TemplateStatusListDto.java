package com.cairn.waypoint.dashboard.endpoints.templatestatus.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TemplateStatusListDto {

  private List<TemplateStatusDto> statuses;
  private Integer numOfStatuses;

  public Integer getNumOfStatuses() {
    return statuses.size();
  }
}
