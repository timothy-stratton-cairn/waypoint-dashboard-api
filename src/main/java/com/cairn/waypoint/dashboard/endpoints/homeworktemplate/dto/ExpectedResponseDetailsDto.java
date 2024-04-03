package com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpectedResponseDetailsDto {

  private String response;
  private String tooltip;
}
