package com.cairn.waypoint.dashboard.entity.enumeration;

import com.cairn.waypoint.dashboard.entity.StepStatus;
import com.cairn.waypoint.dashboard.service.StepStatusService;
import jakarta.persistence.EntityNotFoundException;

public enum StepStatusEnum {
  TODO, IN_PROGRESS, DONE;

  protected StepStatus instance;

  public StepStatus getInstance() {
    if (this.instance == null) {
      this.instance = StepStatusService.availableStatuses.stream()
          .filter(stepStatus ->
              stepStatus.getName().toUpperCase().replace(' ', '_')
                  .equals(this.name()))
          .findFirst()
          .orElseThrow(EntityNotFoundException::new);
    }
    return this.instance;
  }
}
