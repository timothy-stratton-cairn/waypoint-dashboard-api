package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import com.cairn.waypoint.dashboard.entity.ProtocolCommentType;
import com.cairn.waypoint.dashboard.entity.enumeration.ProtocolCommentTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppendProtocolCommentaryDto {

  private String goalProgress;
  private String comment;
  private String commentType;

  @JsonIgnore
  private ProtocolCommentTypeEnum protocolCommentType;
}
