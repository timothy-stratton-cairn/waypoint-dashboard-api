package com.cairn.waypoint.dashboard.endpoints.ops.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientCreationResponseListDto {

  private List<ClientCreationResponseDto> clientCreationResponses;
  private String overallStatus;
  private Integer numberOfAccounts;

  public Integer getNumberOfAccounts() {
    return clientCreationResponses.size();
  }

  public String getOverallStatus() {
    return clientCreationResponses.stream()
        .anyMatch(ClientCreationResponseDto::getError) ? "FAILED" : "SUCCESS";
  }
}
