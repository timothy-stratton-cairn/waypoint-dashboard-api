package com.cairn.waypoint.dashboard.entity.enumeration;

import com.cairn.waypoint.dashboard.entity.TemplateStatus;
import com.cairn.waypoint.dashboard.service.data.TemplateStatusDataService;
import jakarta.persistence.EntityNotFoundException;

public enum TemplateStatusEnum {
  INACTIVE, LIVE, ARCHIVED;

  private TemplateStatus instance;

  public TemplateStatus getInstance() {
    if (this.instance == null) {
      this.instance = TemplateStatusDataService.availableStatuses.stream()
          .filter(templateStatus ->
              templateStatus.getName().toUpperCase().replace(' ', '_')
                  .equals(this.name()))
          .findFirst()
          .orElseThrow(EntityNotFoundException::new);
    }
    return this.instance;
  }
}
