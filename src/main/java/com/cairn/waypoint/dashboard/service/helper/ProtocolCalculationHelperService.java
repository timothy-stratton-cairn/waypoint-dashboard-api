package com.cairn.waypoint.dashboard.service.helper;

import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolStep;
import com.cairn.waypoint.dashboard.entity.StepStatus;
import com.cairn.waypoint.dashboard.entity.enumeration.StepStatusEnum;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ProtocolCalculationHelperService {

  public static BigDecimal getProtocolCompletionPercentage(Protocol returnedProtocol) {
    if (!returnedProtocol.getProtocolSteps().isEmpty()) {
      return returnedProtocol.getProtocolSteps().stream()
          .map(ProtocolStep::getStatus)
          .map(StepStatusEnum::getInstance)
          .map(StepStatus::getWeight)
          .reduce(BigDecimal.ZERO, BigDecimal::add)
          .divide(new BigDecimal(returnedProtocol.getProtocolSteps().size()), RoundingMode.HALF_UP);
    } else {
      return new BigDecimal(0);
    }

  }
}
