package com.cairn.waypoint.dashboard.endpoints.ops.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientCreationResponseListDto {

  private List<ClientCreationResponseDto> accountCreationResponses;
  private String overallStatus;
  private Integer numberOfAccounts;

  public Integer getNumberOfAccounts() {
    return accountCreationResponses.size();
  }

  public String getOverallStatus() {
    return accountCreationResponses.stream()
        .anyMatch(ClientCreationResponseDto::getError) ? "FAILED" : "SUCCESS";
  }
}
