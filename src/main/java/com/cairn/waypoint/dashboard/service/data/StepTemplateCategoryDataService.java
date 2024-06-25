package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.entity.StepTemplateCategory;
import com.cairn.waypoint.dashboard.repository.StepTemplateCategoryRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class StepTemplateCategoryDataService {

  private final StepTemplateCategoryRepository stepTemplateCategoryRepository;

  public StepTemplateCategoryDataService(
      StepTemplateCategoryRepository stepTemplateCategoryRepository) {
    this.stepTemplateCategoryRepository = stepTemplateCategoryRepository;
  }

  public List<StepTemplateCategory> getAllParentTemplateCategories() {
    return this.stepTemplateCategoryRepository.findByParentCategoryIsNull();
  }

  public Optional<StepTemplateCategory> getTemplateCategoryById(Long stepTemplateCategoryId) {
    return this.stepTemplateCategoryRepository.findById(stepTemplateCategoryId);
  }

  public Optional<StepTemplateCategory> findByName(String categoryName) {
    return this.stepTemplateCategoryRepository.findByName(categoryName);
  }

  public StepTemplateCategory saveTemplateCategory(StepTemplateCategory stepTemplateCategory) {
    return this.stepTemplateCategoryRepository.save(stepTemplateCategory);
  }
}
