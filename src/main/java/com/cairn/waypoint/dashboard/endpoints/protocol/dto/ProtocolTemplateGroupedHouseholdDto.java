package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import com.cairn.waypoint.dashboard.dto.HouseholdAccountListDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtocolTemplateGroupedHouseholdDto {

  private Long id;
  private String householdName;
  private HouseholdAccountListDto householdAccountList;
}
