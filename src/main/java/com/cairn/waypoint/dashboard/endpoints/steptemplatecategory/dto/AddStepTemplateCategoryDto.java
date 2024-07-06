package com.cairn.waypoint.dashboard.endpoints.steptemplatecategory.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddStepTemplateCategoryDto {

  private String name;
  private String description;
  private Long parentCategoryId;
  private Long responsibleUserId;
  private Long accountableUserId;
  private Long consultedUserId;
  private Long informedUserId;
  private Long responsibleRoleId;
  private Long accountableRoleId;
  private Long consultedRoleId;
  private Long informedRoleId;
}
