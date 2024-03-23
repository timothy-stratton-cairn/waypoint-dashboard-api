package com.cairn.waypoint.dashboard.entity.converter;

import com.cairn.waypoint.dashboard.entity.StepStatus;
import com.cairn.waypoint.dashboard.entity.enumeration.StepStatusEnum;
import com.cairn.waypoint.dashboard.service.StepStatusService;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.persistence.EntityNotFoundException;
import java.util.Objects;

@Converter
public class StepStatusConverter implements AttributeConverter<StepStatusEnum, Long> {

  @Override
  public Long convertToDatabaseColumn(StepStatusEnum stepStatusEnum) {
    return stepStatusEnum.getInstance().getId();
  }

  @Override
  public StepStatusEnum convertToEntityAttribute(Long id) {
    StepStatus retrievedStatus = StepStatusService.availableStatuses.stream()
        .filter(stepStatus -> Objects.equals(stepStatus.getId(), id))
        .findFirst()
        .orElseThrow(EntityNotFoundException::new);

    return StepStatusEnum.valueOf(retrievedStatus.getName().toUpperCase().replace(' ', '_'));
  }
}
