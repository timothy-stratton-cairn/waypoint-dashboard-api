package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.entity.TemplateCategory;
import com.cairn.waypoint.dashboard.entity.enumeration.TemplateCategoryEnum;
import com.cairn.waypoint.dashboard.repository.TemplateCategoryRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TemplateCategoryDataService {

  public static final List<TemplateCategory> availableCategories = new ArrayList<>();

  @SuppressWarnings({"unused", "UnusedAssignment"})
  public TemplateCategoryDataService(
      TemplateCategoryRepository templateCategoryRepository) {
    TemplateCategoryDataService.availableCategories.addAll(templateCategoryRepository.findAll());

    //Initialize the Enum
    TemplateCategoryEnum unused = TemplateCategoryEnum.LIFECYCLE;
    unused = TemplateCategoryEnum.EVENT_DRIVEN;
  }
}
