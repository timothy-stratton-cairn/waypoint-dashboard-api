package com.cairn.waypoint.dashboard.dto.authorization;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRolesListDto {

  private List<String> roles;

  public Integer getNumOfRoles() {
    return roles != null ? roles.size() : 0;
  }

}
