package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtocolCommentListDto {

  List<ProtocolCommentDto> comments;
  private Integer numOfComments;

  public Integer getNumOfComments() {
    return comments.size();
  }
}
