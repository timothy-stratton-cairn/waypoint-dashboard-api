package com.cairn.waypoint.dashboard.entity.converter;

import com.cairn.waypoint.dashboard.entity.ProtocolCommentType;
import com.cairn.waypoint.dashboard.entity.enumeration.ProtocolCommentTypeEnum;
import com.cairn.waypoint.dashboard.service.data.ProtocolCommentTypeDataService;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.persistence.EntityNotFoundException;
import java.util.Objects;

@Converter
public class ProtocolCommentTypeConverter implements AttributeConverter<ProtocolCommentTypeEnum, Long> {

  @Override
  public Long convertToDatabaseColumn(ProtocolCommentTypeEnum protocolCommentTypeEnum) {
    return protocolCommentTypeEnum.getInstance().getId();
  }

  @Override
  public ProtocolCommentTypeEnum convertToEntityAttribute(Long id) {
    ProtocolCommentType retrievedCommentType = ProtocolCommentTypeDataService.availableCommentTypes.stream()
        .filter(protocolCommentType -> Objects.equals(protocolCommentType.getId(), id))
        .findFirst()
        .orElseThrow(EntityNotFoundException::new);

    return ProtocolCommentTypeEnum.valueOf(retrievedCommentType.getName().toUpperCase().replace(' ', '_'));
  }
}
