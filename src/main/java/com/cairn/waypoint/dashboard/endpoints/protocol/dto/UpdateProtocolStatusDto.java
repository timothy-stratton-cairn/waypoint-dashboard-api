package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProtocolStatusDto {

  private String newProtocolStatus;
  private String conditionalCompletionComment;
}
