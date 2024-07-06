package com.cairn.waypoint.dashboard.endpoints.recurrencetype.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecurrenceTypeDto {

  private Long id;
  private String recurrenceType;
  private String description;
}
