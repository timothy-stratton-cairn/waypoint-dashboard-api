package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import com.cairn.waypoint.dashboard.dto.serializer.BigDecimalSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountProtocolDto {

  private Long id;
  private String name;

  @JsonSerialize(using = BigDecimalSerializer.class)
  private BigDecimal completionPercentage;
  private AssociatedStepsListDto associatedSteps;
}
