package com.cairn.waypoint.dashboard.endpoints.protocol.service;

import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolStep;
import com.cairn.waypoint.dashboard.entity.StepStatus;
import com.cairn.waypoint.dashboard.entity.enumeration.StepStatusEnum;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ProtocolCalculationService {

  public static BigDecimal getProtocolCompletionPercentage(Protocol returnedProtocol) {
    return returnedProtocol.getProtocolSteps().stream()
        .map(ProtocolStep::getStatus)
        .map(StepStatusEnum::getInstance)
        .map(StepStatus::getWeight)
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .divide(new BigDecimal(returnedProtocol.getProtocolSteps().size()), RoundingMode.HALF_UP);

  }
}
