package com.cairn.waypoint.dashboard.endpoints.steptemplate.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StepTemplateListDto {
  private List<StepTemplateDto> stepTemplates;
  private Integer numOfStepTemplates;

  public Integer getNumOfStepTemplates() {
    return stepTemplates.size();
  }
}
