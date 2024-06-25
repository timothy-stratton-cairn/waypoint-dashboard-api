package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecurrenceDetailsDto {

  private String recurrenceType;
  private String triggeringStatus;
  private Integer willReoccurInYears;
  private Integer willReoccurInMonths;
  private Integer willReoccurInDays;
}
