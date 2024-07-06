package com.cairn.waypoint.dashboard.service.helper;

import com.cairn.waypoint.dashboard.entity.Homework;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolStep;
import com.cairn.waypoint.dashboard.entity.ProtocolStepLinkedHomework;
import com.cairn.waypoint.dashboard.entity.StepStatus;
import com.cairn.waypoint.dashboard.entity.enumeration.StepStatusEnum;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProtocolCalculationHelperService {

  public static BigDecimal getProtocolCompletionPercentage(Protocol returnedProtocol) {
    if (!returnedProtocol.getProtocolSteps().isEmpty()) {
      List<BigDecimal> stepCompletionPercentages = returnedProtocol.getProtocolSteps().stream()
          .filter(protocolStep -> protocolStep.getLinkedHomework() == null
              || protocolStep.getLinkedHomework()
              .isEmpty() || protocolStep.getStatus().equals(StepStatusEnum.CONDITIONAL_COMPLETION)
              || protocolStep.getStatus().equals(StepStatusEnum.DONE))
          .map(ProtocolStep::getStatus)
          .map(StepStatusEnum::getInstance)
          .map(StepStatus::getWeight)
          .collect(Collectors.toList());

      List<BigDecimal> stepHomeworkCompletionPercentages;

      try {
        stepHomeworkCompletionPercentages = returnedProtocol.getProtocolSteps()
            .stream()
            .map(ProtocolStep::getLinkedHomework)
            .flatMap(Set::stream)
            .map(ProtocolStepLinkedHomework::getHomework)
            .filter(Objects::nonNull)
            .map(Homework::getHomeworkQuestions)
            .map(homeworkResponses -> {
              long responseCount = homeworkResponses.stream()
                  .filter(homeworkResponse -> homeworkResponse.getHomeworkQuestion().getRequired()
                      && homeworkResponse.getResponse() != null && !homeworkResponse.getResponse()
                      .isEmpty())
                  .count();
              long requiredCount = homeworkResponses.stream()
                  .filter(homeworkResponse -> homeworkResponse.getHomeworkQuestion().getRequired())
                  .count();

              return BigDecimal.valueOf((double) responseCount / requiredCount);
            })
            .collect(Collectors.toList());
      } catch (NullPointerException e) {
        stepHomeworkCompletionPercentages = Arrays.asList(BigDecimal.ZERO);
      }

      return Stream.of(stepCompletionPercentages, stepHomeworkCompletionPercentages)
          .flatMap(Collection::stream)
          .reduce(BigDecimal.ZERO, BigDecimal::add)
          .divide(new BigDecimal(returnedProtocol.getProtocolSteps().size()), RoundingMode.HALF_UP);
    } else {
      return new BigDecimal(0);
    }

  }
}
