package com.cairn.waypoint.dashboard.dto.authorization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdDetailsDto {

  private Long id;
  private String name;
  private String description;
  private PrimaryContactDetailsListDto primaryContacts;
  private HouseholdAccountDetailsListDto householdAccounts;
}
