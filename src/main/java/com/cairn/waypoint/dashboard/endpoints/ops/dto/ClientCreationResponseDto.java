package com.cairn.waypoint.dashboard.endpoints.ops.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientCreationResponseDto {

  private Long accountId;
  private Long householdId;
  private String username;
  private Boolean error;
  private String message;
}
