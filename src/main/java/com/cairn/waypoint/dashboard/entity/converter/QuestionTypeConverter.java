package com.cairn.waypoint.dashboard.entity.converter;

import com.cairn.waypoint.dashboard.entity.QuestionType;
import com.cairn.waypoint.dashboard.entity.enumeration.QuestionTypeEnum;
import com.cairn.waypoint.dashboard.service.QuestionTypeService;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.persistence.EntityNotFoundException;
import java.util.Objects;

@Converter
public class QuestionTypeConverter implements AttributeConverter<QuestionTypeEnum, Long> {

  @Override
  public Long convertToDatabaseColumn(QuestionTypeEnum questionTypeEnum) {
    return questionTypeEnum.getInstance().getId();
  }

  @Override
  public QuestionTypeEnum convertToEntityAttribute(Long id) {
    QuestionType retrievedStatus = QuestionTypeService.availableTypes.stream()
        .filter(questionType -> Objects.equals(questionType.getId(), id))
        .findFirst()
        .orElseThrow(EntityNotFoundException::new);

    return QuestionTypeEnum.valueOf(retrievedStatus.getType().toUpperCase().replace(' ', '_').replace('-', '_'));
  }
}
