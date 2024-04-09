package com.cairn.waypoint.dashboard.endpoints.dashboard.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssociatedAccountDto {

  private Long id;
  private String firstName;
  private String lastName;

}
