package com.cairn.waypoint.dashboard.endpoints.householdgoalstemplate.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HouseholdGoalTemplateListDto {

  List<HouseholdGoalTemplateDto> templates;
  Integer totalTemplate;

  public void setTotalTemplate(Integer totalTemplate) {
    this.totalTemplate = templates.size();
  }
}
