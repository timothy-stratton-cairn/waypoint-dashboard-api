package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.entity.StepStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StepStatusRepository extends JpaRepository<StepStatus, Long> {
}
