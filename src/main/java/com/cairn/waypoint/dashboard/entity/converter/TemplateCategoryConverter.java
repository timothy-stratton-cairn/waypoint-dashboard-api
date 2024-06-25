package com.cairn.waypoint.dashboard.entity.converter;

import com.cairn.waypoint.dashboard.entity.TemplateCategory;
import com.cairn.waypoint.dashboard.entity.enumeration.TemplateCategoryEnum;
import com.cairn.waypoint.dashboard.service.data.TemplateCategoryDataService;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.persistence.EntityNotFoundException;
import java.util.Objects;

@Converter
public class TemplateCategoryConverter implements AttributeConverter<TemplateCategoryEnum, Long> {

  @Override
  public Long convertToDatabaseColumn(TemplateCategoryEnum templateCategoryEnum) {
    return templateCategoryEnum.getInstance().getId();
  }

  @Override
  public TemplateCategoryEnum convertToEntityAttribute(Long id) {
    if (id != null) {
      TemplateCategory retrievedCategory = TemplateCategoryDataService.availableCategories.stream()
          .filter(templateCategory -> Objects.equals(templateCategory.getId(), id))
          .findFirst()
          .orElseThrow(EntityNotFoundException::new);

      return TemplateCategoryEnum.valueOf(
          retrievedCategory.getName().toUpperCase().replace(' ', '_'));
    } else {
      return null;
    }
  }
}
