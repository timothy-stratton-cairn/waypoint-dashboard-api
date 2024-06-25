package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.entity.StepTemplateCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StepTemplateCategoryRepository extends JpaRepository<StepTemplateCategory, Long> {

  Optional<StepTemplateCategory> findByName(String name);

  List<StepTemplateCategory> findByParentCategoryIsNull();
}
