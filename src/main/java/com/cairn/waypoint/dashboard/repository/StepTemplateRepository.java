package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.entity.StepTemplate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StepTemplateRepository extends JpaRepository<StepTemplate, Long> {

  Optional<StepTemplate> findByName(String name);
}
