package com.cairn.waypoint.dashboard.endpoints.householdgoals.dto;


import com.cairn.waypoint.dashboard.endpoints.protocol.dto.AccountProtocolDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GoalProtocolsDto {

  private Long goalId;
  private String goalName;
  private String goalDescription;
  private String goalCategory; // Using GoalCategory as a string or enum, based on your need
  private List<AccountProtocolDto> protocols; // Directly use AccountProtocolDto for protocols
}

