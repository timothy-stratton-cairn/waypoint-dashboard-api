package com.cairn.waypoint.dashboard.entity.converter;

import com.cairn.waypoint.dashboard.entity.TemplateStatus;
import com.cairn.waypoint.dashboard.entity.enumeration.TemplateStatusEnum;
import com.cairn.waypoint.dashboard.service.data.TemplateStatusDataService;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.persistence.EntityNotFoundException;
import java.util.Objects;

@Converter
public class TemplateStatusConverter implements AttributeConverter<TemplateStatusEnum, Long> {

  @Override
  public Long convertToDatabaseColumn(TemplateStatusEnum templateStatusEnum) {
    return templateStatusEnum.getInstance().getId();
  }

  @Override
  public TemplateStatusEnum convertToEntityAttribute(Long id) {
    TemplateStatus retrievedStatus = TemplateStatusDataService.availableStatuses.stream()
        .filter(templateStatus -> Objects.equals(templateStatus.getId(), id))
        .findFirst()
        .orElseThrow(EntityNotFoundException::new);

    return TemplateStatusEnum.valueOf(retrievedStatus.getName().toUpperCase().replace(' ', '_'));
  }
}
