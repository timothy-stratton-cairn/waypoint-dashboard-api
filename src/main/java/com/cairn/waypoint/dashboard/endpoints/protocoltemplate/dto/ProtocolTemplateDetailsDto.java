package com.cairn.waypoint.dashboard.endpoints.protocoltemplate.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtocolTemplateDetailsDto {

  private Long id;
  private String name;
  private String description;
  private String status;
  private String templateCategory;
  private Integer defaultDueByInYears;
  private Integer defaultDueByInMonths;
  private Integer defaultDueByInDays;
  private RecurrenceTypeDetailsDto defaultProtocolRecurrence;
  private AssociatedStepTemplatesListDto associatedSteps;
}
