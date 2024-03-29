package com.cairn.waypoint.dashboard.endpoints.protocoltemplate.dto;

import java.util.LinkedHashSet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddProtocolTemplateDetailsDto {

  private String name;
  private String description;
  private LinkedHashSet<Long> associatedStepTemplateIds;
}
