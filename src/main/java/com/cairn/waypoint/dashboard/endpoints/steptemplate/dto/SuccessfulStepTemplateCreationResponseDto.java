package com.cairn.waypoint.dashboard.endpoints.steptemplate.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SuccessfulStepTemplateCreationResponseDto {

  private Long stepTemplateId;
  private String stepTemplateName;
  private String stepTemplateDescription;
}
