package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import com.cairn.waypoint.dashboard.entity.enumeration.ProtocolCommentTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
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
  private String commentType;
  private Long assignedHouseholdId;
  private LocalDate dueDate;

  @JsonIgnore
  private ProtocolCommentTypeEnum protocolCommentType;
}
