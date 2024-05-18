package com.cairn.waypoint.dashboard.endpoints.protocolcommenttype.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtocolCommentTypeListDto {

  private List<ProtocolCommentTypeDto> commentTypes;
  private Integer numOfCommentTypes;

  public Integer getNumOfCommentTypes() {
    return commentTypes.size();
  }
}
