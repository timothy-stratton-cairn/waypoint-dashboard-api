package com.cairn.waypoint.dashboard.entity.converter;

import com.cairn.waypoint.dashboard.entity.RecurrenceType;
import com.cairn.waypoint.dashboard.entity.enumeration.RecurrenceTypeEnum;
import com.cairn.waypoint.dashboard.service.data.RecurrenceTypeDataService;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.persistence.EntityNotFoundException;
import java.util.Objects;

@Converter
public class RecurrenceTypeConverter implements AttributeConverter<RecurrenceTypeEnum, Long> {

  @Override
  public Long convertToDatabaseColumn(RecurrenceTypeEnum protocolStatusEnum) {
    return protocolStatusEnum.getInstance().getId();
  }

  @Override
  public RecurrenceTypeEnum convertToEntityAttribute(Long id) {
    if (id != null) {
      RecurrenceType retrievedRecurrenceType = RecurrenceTypeDataService.availableStatuses.stream()
          .filter(recurrenceType -> Objects.equals(recurrenceType.getId(), id))
          .findFirst()
          .orElseThrow(EntityNotFoundException::new);

      return RecurrenceTypeEnum.valueOf(
          retrievedRecurrenceType.getName().toUpperCase().replace(' ', '_'));
    } else {
      return null;
    }
  }
}
