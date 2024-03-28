package com.cairn.waypoint.dashboard.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountDto {

  private Long id;
  private String firstName;
  private String lastName;
}
