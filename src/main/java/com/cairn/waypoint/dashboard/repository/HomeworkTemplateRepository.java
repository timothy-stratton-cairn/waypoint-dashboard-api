package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.entity.HomeworkTemplate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeworkTemplateRepository extends JpaRepository<HomeworkTemplate, Long> {

  Optional<HomeworkTemplate> findByName(String name);
}
