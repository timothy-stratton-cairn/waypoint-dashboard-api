package com.cairn.waypoint.dashboard.dto.authorization;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LinkedAccountDetailsDto {

  private Long id;
  private String firstName;
  private String lastName;
  private String username;
}
