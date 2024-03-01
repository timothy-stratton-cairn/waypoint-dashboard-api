package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.entity.StepTask;
import com.cairn.waypoint.dashboard.entity.StepTemplate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StepTaskRepository extends JpaRepository<StepTask, Long> {

  Optional<StepTask> findByName(String name);
}
