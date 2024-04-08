package com.cairn.waypoint.dashboard.endpoints.homework.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomeworkUserListDto {

  private List<Long> userIds;
  private Integer numOfUsers;

  public Integer getNumOfUsers() {
    return userIds.size();
  }
}
