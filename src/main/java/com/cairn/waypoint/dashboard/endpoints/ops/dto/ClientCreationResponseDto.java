package com.cairn.waypoint.dashboard.endpoints.ops.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientCreationResponseDto {

  private Long accountId;
  private String username;
  private Boolean error;
  private String message;
}
