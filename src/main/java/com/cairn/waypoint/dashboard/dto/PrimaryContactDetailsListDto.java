package com.cairn.waypoint.dashboard.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrimaryContactDetailsListDto {

  private List<PrimaryContactDetailsDto> accounts;
  private Integer numberOfAccounts;

  private Integer getNumberOfAccounts() {
    return accounts != null ? accounts.size() : 0;
  }
}