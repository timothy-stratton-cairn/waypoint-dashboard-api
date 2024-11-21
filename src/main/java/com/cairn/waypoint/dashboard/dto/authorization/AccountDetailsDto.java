package com.cairn.waypoint.dashboard.dto.authorization;

import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountDetailsDto {
  private Long id;
  private String username;
  private String firstName;
  private String lastName;
  private Set<String> roles;
  private String email;
  private LinkedAccountDetailsDto coClient;
  private Set<LinkedAccountDetailsDto> dependents;
  private Long householdId;
}
