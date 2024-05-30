package com.cairn.waypoint.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdAccountDetailsDto {

  private Long clientAccountId;
  private String firstName;
  private String lastName;
}
