package com.cairn.waypoint.dashboard.endpoints.protocoltemplate.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssociatedStepTemplatesListDto {

  List<ProtocolStepTemplateDto> steps;
  private Integer numOfSteps;

  public Integer getNumOfSteps() {
    if (steps == null) {
      return 0;
    }
    return steps.size();
  }
}
