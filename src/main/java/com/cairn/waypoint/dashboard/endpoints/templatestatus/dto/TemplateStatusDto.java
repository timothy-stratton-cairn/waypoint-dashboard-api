package com.cairn.waypoint.dashboard.endpoints.templatestatus.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TemplateStatusDto {

  private Long id;
  private String status;
  private String description;
}
