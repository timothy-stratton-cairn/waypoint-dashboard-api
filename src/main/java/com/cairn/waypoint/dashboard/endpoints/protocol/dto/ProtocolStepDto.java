package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtocolStepDto {

  private Long id;
  private String name;
}
