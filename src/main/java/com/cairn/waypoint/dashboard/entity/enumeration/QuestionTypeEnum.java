package com.cairn.waypoint.dashboard.entity.enumeration;

import com.cairn.waypoint.dashboard.entity.QuestionType;
import com.cairn.waypoint.dashboard.service.QuestionTypeService;
import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.EntityNotFoundException;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public enum QuestionTypeEnum {
  STRING() {
    @Override
    public String createInstance(String... value) {
      return value[0];
    }
  },
  INTEGER() {
    @Override
    public Integer createInstance(String... value) {
      return Integer.valueOf(value[0]);
    }
  },
  FLOAT() {
    @Override
    public Float createInstance(String... value) {
      return Float.valueOf(value[0]);
    }
  },
  MULTI_SELECT_OPTION() {
    @Override
    public List<String> createInstance(String... value) {
      return List.of(value);
    }
  },
  SELECT_OPTION() {
    @Override
    public List<String> createInstance(String... value) {
      return List.of(value);
    }
  },
  DATE() {
    @Override
    public LocalDate createInstance(String... value) {
      return LocalDate.parse(value[0]);
    }
  },
  DATETIME() {
    @Override
    public LocalDateTime createInstance(String... value) {
      return LocalDateTime.parse(value[0]);
    }
  },
  BOOLEAN() {
    @Override
    public Boolean createInstance(String... value) {
      return Boolean.parseBoolean(value[0]);
    }
  },
  FILE() {
    @Override
    public File createInstance(String... value) {
      return new File(value[0]);
    }
  };

  private QuestionType instance;

  @JsonCreator
  public static QuestionTypeEnum create(String value) {
    if (value == null) {
      throw new IllegalArgumentException();
    }
    for (QuestionTypeEnum v : values()) {
      if (value.equals(v.name())) {
        return v;
      }
    }
    throw new IllegalArgumentException();
  }

  public QuestionType getInstance() {
    if (this.instance == null) {
      this.instance = QuestionTypeService.availableTypes.stream()
          .filter(questionType ->
              questionType.getType().toUpperCase()
                  .replace(' ', '_')
                  .replace('-', '_')
                  .equals(this.name()))
          .findFirst()
          .orElseThrow(EntityNotFoundException::new);
    }
    return this.instance;
  }

  public Class<?> getDataType() {
    return this.getInstance()//call get instance here to ensure instantiation
        .getTypeClassReference();
  }

  public abstract Object createInstance(String... value);
}
