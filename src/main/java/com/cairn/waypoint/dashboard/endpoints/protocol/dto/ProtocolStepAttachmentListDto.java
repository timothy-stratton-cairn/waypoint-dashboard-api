package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtocolStepAttachmentListDto {

  List<ProtocolStepAttachmentDto> attachments;
  private Integer numOfAttachments;

  public Integer getNumOfAttachments() {
    if (attachments == null) {
      return 0;
    }
    return attachments.size();
  }
}
