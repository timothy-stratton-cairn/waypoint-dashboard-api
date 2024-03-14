package com.cairn.waypoint.dashboard.service;

import com.cairn.waypoint.dashboard.entity.TemplateCategory;
import com.cairn.waypoint.dashboard.repository.TemplateCategoryRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TemplateCategoryService {
  private TemplateCategoryRepository templateCategoryRepository;

  public TemplateCategoryService(TemplateCategoryRepository templateCategoryRepository) {
    this.templateCategoryRepository = templateCategoryRepository;
  }

  public List<TemplateCategory> getAllTemplateCategories() {
    return this.templateCategoryRepository.findAll();
  }

  public Optional<TemplateCategory> getTemplateCategoryById(Long stepTemplateCategoryId) {
    return this.templateCategoryRepository.findById(stepTemplateCategoryId);
  }

  public Optional<TemplateCategory> findByName(String categoryName) {
    return this.templateCategoryRepository.findByName(categoryName);
  }
}
