package com.cairn.waypoint.dashboard.endpoints.protocolstep.dto;

import com.cairn.waypoint.dashboard.entity.enumeration.StepStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProtocolStepStatusDto {

  private StepStatusEnum status;
}
