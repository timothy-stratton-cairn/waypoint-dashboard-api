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
public class RoleListDto {

  private List<RoleDto> roles;
  private Integer numOfRoles;

  public Integer getNumOfRoles() {
    return roles.size();
  }
}
