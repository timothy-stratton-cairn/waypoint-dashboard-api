package com.cairn.waypoint.dashboard.endpoints.steptemplate.dto;

import com.cairn.waypoint.dashboard.entity.enumeration.TemplateStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
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
  private String status;
  private List<Long> linkedHomeworkTemplateIds;
  private Long stepTemplateCategoryId;

  @JsonIgnore
  public TemplateStatusEnum getStatus() {
    try {
      return TemplateStatusEnum.valueOf(status);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
}
