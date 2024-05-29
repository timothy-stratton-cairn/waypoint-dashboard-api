package com.cairn.waypoint.dashboard.endpoints.homework.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpectedResponseDto {

  private String response;
  private String tooltip;
}
