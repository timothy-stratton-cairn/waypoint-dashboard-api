package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtocolTemplateGroupedAccountDto {

  private Long id;
  private String firstName;
  private String lastName;
}
