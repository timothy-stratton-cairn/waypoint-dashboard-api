package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import com.cairn.waypoint.dashboard.entity.enumeration.ProtocolCommentTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateProtocolDetailsDto {

  private String protocolName;
  private String goal;
  private String goalProgress;
  private String comment;
  private String commentType;
  private Boolean markForAttention;
  private LocalDate dueDate;
  private List<UpdateProtocolStepDetailsDto> protocolSteps;

  @JsonIgnore
  private ProtocolCommentTypeEnum protocolCommentType;
}
