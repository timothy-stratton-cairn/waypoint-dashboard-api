package com.cairn.waypoint.dashboard.endpoints.protocoltemplate.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtocolStepTemplateDto {

  private Long id;
  private String name;
  private String description;
  private StepTemplateCategoryDto category;
}
