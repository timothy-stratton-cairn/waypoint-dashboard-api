package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssociatedUsersListDto {

  List<Long> userIds;
  private Integer numOfUsers;

  public Integer getNumOfUsers() {
    return userIds.size();
  }
}
