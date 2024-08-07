package com.cairn.waypoint.dashboard.dto.authorization;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountDto {

  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private Long householdId;
  private AccountRolesListDto accountRoles;
}
