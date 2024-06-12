package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LinkedHomeworksDto {

  private Set<Long> homeworkIds;
  private Integer numberOfHomeworks;

  public Integer getNumberOfHomeworks() {
    return homeworkIds != null ? homeworkIds.size() : 0;
  }
}
