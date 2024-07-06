package com.cairn.waypoint.dashboard.entity.enumeration;

import com.cairn.waypoint.dashboard.entity.ProtocolStatus;
import com.cairn.waypoint.dashboard.service.data.ProtocolStatusDataService;
import jakarta.persistence.EntityNotFoundException;

public enum ProtocolStatusEnum {
  IN_PROGRESS, COMPLETED, COMPLETED_WITH_TRIGGERED_RECURRENCE, COMPLETED_CONDITIONALLY, ARCHIVED;

  private ProtocolStatus instance;

  public ProtocolStatus getInstance() {
    if (this.instance == null) {
      this.instance = ProtocolStatusDataService.availableStatuses.stream()
          .filter(protocolStatus ->
              protocolStatus.getName().toUpperCase().replace(' ', '_')
                  .equals(this.name()))
          .findFirst()
          .orElseThrow(EntityNotFoundException::new);
    }
    return this.instance;
  }
}
