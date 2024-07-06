package com.cairn.waypoint.dashboard.endpoints.stepstatus.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StepStatusDto {

  private Long id;
  private String status;
  private String description;
}
