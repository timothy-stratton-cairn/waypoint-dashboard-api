package com.cairn.waypoint.dashboard.dto.authorization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrimaryContactDetailsDto {

  private Long accountId;
  private String firstName;
  private String lastName;
  private String phoneNumber;
  private String email;
}
