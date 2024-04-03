package com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TriggeredProtocolTemplateDetailsDto {

  private Long id;
  private String name;
  private String description;
}
