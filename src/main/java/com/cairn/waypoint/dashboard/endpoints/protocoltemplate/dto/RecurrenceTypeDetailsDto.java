package com.cairn.waypoint.dashboard.endpoints.protocoltemplate.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecurrenceTypeDetailsDto {

  private String recurrenceType;
  private String defaultTriggeringStatus;
  private Integer defaultReoccurInYears;
  private Integer defaultReoccurInMonths;
  private Integer defaultReoccurInDays;
}
