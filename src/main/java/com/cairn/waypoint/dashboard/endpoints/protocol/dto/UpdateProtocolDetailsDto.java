package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateProtocolDetailsDto {

  private String goal;
  private String goalProgress;
  private String comment;
  private Boolean markForAttention;
  private LocalDate dueDate;
  private List<UpdateProtocolStepDetailsDto> protocolSteps;
}
