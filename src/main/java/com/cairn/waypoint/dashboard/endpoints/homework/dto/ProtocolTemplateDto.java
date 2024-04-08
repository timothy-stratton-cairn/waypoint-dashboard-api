package com.cairn.waypoint.dashboard.endpoints.homework.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtocolTemplateDto {

  private Long id;
  private String name;
}
