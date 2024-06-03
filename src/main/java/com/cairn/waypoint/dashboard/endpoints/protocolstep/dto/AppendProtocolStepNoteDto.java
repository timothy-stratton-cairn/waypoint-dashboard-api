package com.cairn.waypoint.dashboard.endpoints.protocolstep.dto;

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
public class AppendProtocolStepNoteDto {

  private String note;
  private String noteType;

  @JsonIgnore
  private ProtocolCommentTypeEnum protocolStepNoteType;
}
