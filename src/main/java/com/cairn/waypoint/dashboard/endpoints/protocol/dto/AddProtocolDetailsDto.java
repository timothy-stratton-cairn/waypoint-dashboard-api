package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddProtocolDetailsDto {

  private Long protocolTemplateId;
  private String goal;
  private String comment;
  private Long associatedAccountId;
}
