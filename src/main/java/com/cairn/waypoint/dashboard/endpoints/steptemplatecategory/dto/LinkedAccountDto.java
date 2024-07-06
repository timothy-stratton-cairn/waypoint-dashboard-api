package com.cairn.waypoint.dashboard.endpoints.steptemplatecategory.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LinkedAccountDto {

  private Long id;
  private String firstName;
  private String lastName;
  private String email;
}
