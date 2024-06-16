package com.cairn.waypoint.dashboard.endpoints.protocoltemplate.dto;

import com.cairn.waypoint.dashboard.entity.enumeration.TemplateStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.LinkedHashSet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProtocolTemplateDetailsDto {

  private String name;
  private String description;
  private String status;
  private LinkedHashSet<Long> associatedStepTemplateIds;

  @JsonIgnore
  public TemplateStatusEnum getStatus() {
    try {
      return TemplateStatusEnum.valueOf(status);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
}
