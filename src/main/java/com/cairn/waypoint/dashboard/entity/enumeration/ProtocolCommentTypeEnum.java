package com.cairn.waypoint.dashboard.entity.enumeration;

import com.cairn.waypoint.dashboard.entity.ProtocolCommentType;
import com.cairn.waypoint.dashboard.service.data.ProtocolCommentTypeDataService;
import jakarta.persistence.EntityNotFoundException;

public enum ProtocolCommentTypeEnum {
  COMMENT, RECOMMENDATION, INTERNAL_MEETING_NOTE;

  private ProtocolCommentType instance;

  public ProtocolCommentType getInstance() {
    if (this.instance == null) {
      this.instance = ProtocolCommentTypeDataService.availableCommentTypes.stream()
          .filter(protocolCommentType ->
              protocolCommentType.getName().toUpperCase().replace(' ', '_')
                  .equals(this.name()))
          .findFirst()
          .orElseThrow(EntityNotFoundException::new);
    }
    return this.instance;
  }
}
