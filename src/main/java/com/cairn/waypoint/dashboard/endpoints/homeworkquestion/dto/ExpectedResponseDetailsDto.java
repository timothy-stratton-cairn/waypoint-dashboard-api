package com.cairn.waypoint.dashboard.endpoints.homeworkquestion.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpectedResponseDetailsDto {

  private Long id;
  private String response;
  private String tooltip;
}
