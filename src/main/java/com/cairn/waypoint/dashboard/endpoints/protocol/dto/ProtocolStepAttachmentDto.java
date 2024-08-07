package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtocolStepAttachmentDto {

  private String filename;
  private String downloadUrl;
}
