package com.cairn.waypoint.dashboard.entity.converter;

import com.cairn.waypoint.dashboard.entity.ProtocolStatus;
import com.cairn.waypoint.dashboard.entity.enumeration.ProtocolStatusEnum;
import com.cairn.waypoint.dashboard.service.data.ProtocolStatusDataService;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.persistence.EntityNotFoundException;
import java.util.Objects;

@Converter
public class ProtocolStatusConverter implements AttributeConverter<ProtocolStatusEnum, Long> {

  @Override
  public Long convertToDatabaseColumn(ProtocolStatusEnum protocolStatusEnum) {
    return protocolStatusEnum.getInstance().getId();
  }

  @Override
  public ProtocolStatusEnum convertToEntityAttribute(Long id) {
    ProtocolStatus retrievedStatus = ProtocolStatusDataService.availableStatuses.stream()
        .filter(protocolStatus -> Objects.equals(protocolStatus.getId(), id))
        .findFirst()
        .orElseThrow(EntityNotFoundException::new);

    return ProtocolStatusEnum.valueOf(retrievedStatus.getName().toUpperCase().replace(' ', '_'));
  }
}
