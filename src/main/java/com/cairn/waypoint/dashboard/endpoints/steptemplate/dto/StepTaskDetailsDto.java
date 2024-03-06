package com.cairn.waypoint.dashboard.endpoints.steptemplate.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StepTaskDetailsDto {

  private Long id;
  private String name;
  private String executableReference;
}
