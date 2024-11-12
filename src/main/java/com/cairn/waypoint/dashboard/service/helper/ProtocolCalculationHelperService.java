package com.cairn.waypoint.dashboard.service.helper;


import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolStep;
import com.cairn.waypoint.dashboard.entity.StepStatus;
import com.cairn.waypoint.dashboard.entity.enumeration.StepStatusEnum;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProtocolCalculationHelperService {

  public static BigDecimal getProtocolCompletionPercentage(Protocol returnedProtocol) {
    if (!returnedProtocol.getProtocolSteps().isEmpty()) {
      List<BigDecimal> stepCompletionPercentages = returnedProtocol.getProtocolSteps().stream()
          .filter(protocolStep ->

              protocolStep.getStatus().equals(StepStatusEnum.DONE))
          .map(ProtocolStep::getStatus)
          .map(StepStatusEnum::getInstance)
          .map(StepStatus::getWeight)
          .collect(Collectors.toList());

      return Stream.of(stepCompletionPercentages)
          .flatMap(Collection::stream)
          .reduce(BigDecimal.ZERO, BigDecimal::add)
          .divide(new BigDecimal(returnedProtocol.getProtocolSteps().size()), RoundingMode.HALF_UP);
    } else {
      return new BigDecimal(0);
    }

  }
}
