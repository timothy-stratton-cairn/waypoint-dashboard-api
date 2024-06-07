package com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateExpectedResponseDetailsDto {

  private Long expectedResponseId;

  @Size(max = 255, message = "Response cannot exceed 255 characters in length")
  private String response;

  @Size(max = 255, message = "Tool Tip cannot exceed 255 characters in length")
  private String tooltip;
}
