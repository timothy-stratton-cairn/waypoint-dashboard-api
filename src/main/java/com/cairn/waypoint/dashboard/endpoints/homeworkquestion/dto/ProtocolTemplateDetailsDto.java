package com.cairn.waypoint.dashboard.endpoints.homeworkquestion.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtocolTemplateDetailsDto {

  private Long id;
  private String name;
  private String description;
}
