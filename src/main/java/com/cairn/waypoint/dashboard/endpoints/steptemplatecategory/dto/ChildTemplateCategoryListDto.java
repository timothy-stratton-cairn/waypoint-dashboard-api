package com.cairn.waypoint.dashboard.endpoints.steptemplatecategory.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChildTemplateCategoryListDto {

  private List<ChildTemplateCategoryDto> templateCategories;
  private Integer numOfCategories;

  public Integer getNumOfCategories() {
    return templateCategories.size();
  }
}
