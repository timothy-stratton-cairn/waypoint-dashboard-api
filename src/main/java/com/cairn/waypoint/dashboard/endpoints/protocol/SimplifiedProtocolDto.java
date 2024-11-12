package com.cairn.waypoint.dashboard.endpoints.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SimplifiedProtocolDto {

  private Long id;
  private String name;
  private String description;
  private String status;
}