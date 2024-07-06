package com.cairn.waypoint.dashboard.entity.enumeration;

import com.cairn.waypoint.dashboard.entity.RecurrenceType;
import com.cairn.waypoint.dashboard.service.data.RecurrenceTypeDataService;
import jakarta.persistence.EntityNotFoundException;

public enum RecurrenceTypeEnum {
  ON_STATUS, ON_SCHEDULE, MANUAL;

  private RecurrenceType instance;

  public RecurrenceType getInstance() {
    if (this.instance == null) {
      this.instance = RecurrenceTypeDataService.availableStatuses.stream()
          .filter(stepStatus ->
              stepStatus.getName().toUpperCase().replace(' ', '_')
                  .equals(this.name()))
          .findFirst()
          .orElseThrow(EntityNotFoundException::new);
    }
    return this.instance;
  }
}
