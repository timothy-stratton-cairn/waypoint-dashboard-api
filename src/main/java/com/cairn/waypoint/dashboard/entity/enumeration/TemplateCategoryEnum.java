package com.cairn.waypoint.dashboard.entity.enumeration;

import com.cairn.waypoint.dashboard.entity.TemplateCategory;
import com.cairn.waypoint.dashboard.service.data.TemplateCategoryDataService;
import jakarta.persistence.EntityNotFoundException;

public enum TemplateCategoryEnum {
  LIFECYCLE, EVENT_DRIVEN;

  private TemplateCategory instance;

  public TemplateCategory getInstance() {
    if (this.instance == null) {
      this.instance = TemplateCategoryDataService.availableCategories.stream()
          .filter(templateCategory ->
              templateCategory.getName().toUpperCase().replace(' ', '_')
                  .equals(this.name()))
          .findFirst()
          .orElseThrow(EntityNotFoundException::new);
    }
    return this.instance;
  }
}
