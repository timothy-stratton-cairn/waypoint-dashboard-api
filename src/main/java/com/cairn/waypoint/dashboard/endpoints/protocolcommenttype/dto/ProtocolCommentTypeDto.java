package com.cairn.waypoint.dashboard.endpoints.protocolcommenttype.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtocolCommentTypeDto {

  private String type;
  private String description;
}
