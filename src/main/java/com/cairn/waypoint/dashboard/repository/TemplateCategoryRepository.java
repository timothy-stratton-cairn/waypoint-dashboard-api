package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.entity.TemplateCategory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateCategoryRepository extends JpaRepository<TemplateCategory, Long> {
  Optional<TemplateCategory> findByName(String name);
}
