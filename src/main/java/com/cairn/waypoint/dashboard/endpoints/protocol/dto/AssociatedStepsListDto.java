package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssociatedStepsListDto {

  List<ProtocolStepDto> steps;
  private Integer numOfSteps;

  public Integer getNumOfSteps() {
    if (steps == null) {
      return 0;
    }
    return steps.size();
  }
}
