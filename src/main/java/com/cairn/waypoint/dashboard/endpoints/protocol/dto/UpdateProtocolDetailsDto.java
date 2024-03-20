package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateProtocolDetailsDto {
  private String comment;
  private Boolean markForAttention;
  private List<UpdateProtocolStepDetailsDto> protocolSteps;
}
