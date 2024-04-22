package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.entity.TemplateCategory;
import com.cairn.waypoint.dashboard.repository.TemplateCategoryRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TemplateCategoryDataService {

  private final TemplateCategoryRepository templateCategoryRepository;

  public TemplateCategoryDataService(TemplateCategoryRepository templateCategoryRepository) {
    this.templateCategoryRepository = templateCategoryRepository;
  }

  public List<TemplateCategory> getAllParentTemplateCategories() {
    return this.templateCategoryRepository.findByParentCategoryIsNull();
  }

  public Optional<TemplateCategory> getTemplateCategoryById(Long stepTemplateCategoryId) {
    return this.templateCategoryRepository.findById(stepTemplateCategoryId);
  }

  public Optional<TemplateCategory> findByName(String categoryName) {
    return this.templateCategoryRepository.findByName(categoryName);
  }

  public TemplateCategory saveTemplateCategory(TemplateCategory templateCategory) {
    return this.templateCategoryRepository.save(templateCategory);
  }
}
