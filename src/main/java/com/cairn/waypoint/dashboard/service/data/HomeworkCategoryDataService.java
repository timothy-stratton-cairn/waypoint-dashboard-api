package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.entity.HomeworkCategory;
import com.cairn.waypoint.dashboard.entity.HomeworkResponse;
import com.cairn.waypoint.dashboard.repository.HomeworkCategoryRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HomeworkCategoryDataService {

  private final HomeworkCategoryRepository homeworkCategoryRepository;

  @Transactional(readOnly = true)
  public List<HomeworkCategory> getAllActiveCategories() {
    return homeworkCategoryRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Optional<HomeworkCategory> getCategoryById(Long id) {
    return homeworkCategoryRepository.findById(id);
  }

  @Transactional
  public HomeworkCategory createCategory(HomeworkCategory homeworkCategory) {
    return homeworkCategoryRepository.save(homeworkCategory);
  }
  public HomeworkCategory updateCategory(HomeworkCategory homeworkCategory) {
    return homeworkCategoryRepository.save(homeworkCategory);
  }

  public HomeworkCategory getHomeowrkCategoryById(Long id) {
    return homeworkCategoryRepository.findById(id).orElse(null);
  }
  public HomeworkCategory getHomeworkCategoryByName(String name) {
    return homeworkCategoryRepository.findByName(name).orElse(null);
  }


  @Transactional
  public HomeworkCategory updateCategory(Long id, HomeworkCategory updatedCategory) {
    return homeworkCategoryRepository.findById(id)
        .map(category -> {
          category.setName(updatedCategory.getName());
          category.setDescription(updatedCategory.getDescription());
          category.setModifiedBy(updatedCategory.getModifiedBy());
          return homeworkCategoryRepository.save(category);
        })
        .orElseThrow(() -> new IllegalArgumentException("Category with ID " + id + " not found"));
  }

  @Transactional
  public void deleteCategory(Long id) {
    homeworkCategoryRepository.findById(id).ifPresent(category -> {
      category.setActive(false);
      homeworkCategoryRepository.save(category);
    });
  }
}
