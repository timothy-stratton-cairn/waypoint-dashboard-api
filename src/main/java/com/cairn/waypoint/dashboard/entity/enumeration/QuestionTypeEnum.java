package com.cairn.waypoint.dashboard.entity.enumeration;

import com.cairn.waypoint.dashboard.entity.QuestionType;
import com.cairn.waypoint.dashboard.entity.StepStatus;
import com.cairn.waypoint.dashboard.service.QuestionTypeService;
import com.cairn.waypoint.dashboard.service.StepStatusService;
import jakarta.persistence.EntityNotFoundException;

public enum QuestionTypeEnum {
  STRING, INTEGER, FLOAT, MULTI_SELECT_OPTION, SELECT_OPTION, DATE, DATETIME, BOOLEAN, FILE;

  private QuestionType instance;

  public QuestionType getInstance() {
    if (this.instance == null) {
      this.instance = QuestionTypeService.availableTypes.stream()
          .filter(questionType ->
              questionType.getType().toUpperCase().replace(' ', '_')
                  .equals(this.name()))
          .findFirst()
          .orElseThrow(EntityNotFoundException::new);
    }
    return this.instance;
  }

  public Class<?> getDataType() {
    return this.instance.getTypeClassReference();
  }
}
