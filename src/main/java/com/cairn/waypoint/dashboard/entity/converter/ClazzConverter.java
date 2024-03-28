package com.cairn.waypoint.dashboard.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter
public class ClazzConverter implements AttributeConverter<Class<?>, String> {

  @Override
  public String convertToDatabaseColumn(Class<?> object) {
    if (object == null) {
      return "";
    }
    return object.getName();
  }

  @Override
  public Class<?> convertToEntityAttribute(String stringJson) {
    if (stringJson == null) {
      return null;
    }
    try {
      return Class.forName(stringJson);
    } catch (ClassNotFoundException e) {
      return null;
    }
  }
}
