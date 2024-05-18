package com.cairn.waypoint.dashboard.endpoints.protocolstatus.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtocolStatusDto {

  private String status;
}
