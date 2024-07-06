package com.cairn.waypoint.dashboard.endpoints.steptemplatecategory.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StepTemplateCategoryDetailsDto {

  private Long id;
  private String name;
  private String description;
  private ParentStepTemplateCategoryDto parentCategory;
  private LinkedAccountDto responsibleUser;
  private LinkedAccountDto accountableUser;
  private LinkedAccountDto consultedUser;
  private LinkedAccountDto informedUser;
  private LinkedRoleDto responsibleRole;
  private LinkedRoleDto accountableRole;
  private LinkedRoleDto consultedRole;
  private LinkedRoleDto informedRole;
}
