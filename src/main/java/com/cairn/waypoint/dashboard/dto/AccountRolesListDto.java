package com.cairn.waypoint.dashboard.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountRolesListDto {

  private List<String> roles;

  public Integer getNumOfRoles() {
    return roles != null ? roles.size() : 0;
  }

}
