package com.cairn.waypoint.dashboard.endpoints.steptemplate.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStepTemplateDetailsDto {
  private String name;
  private String description;
  private Long linkedStepTaskId;
  private Long linkedHomeworkTemplateId;
}
