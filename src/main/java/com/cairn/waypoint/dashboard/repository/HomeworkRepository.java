package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.entity.Homework;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeworkRepository extends JpaRepository<Homework, Long> {

  Optional<Homework> findByAssignedHouseholdId(Long id);
}
