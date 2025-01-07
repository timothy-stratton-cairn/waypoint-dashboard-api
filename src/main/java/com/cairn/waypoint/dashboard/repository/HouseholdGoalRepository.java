package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.entity.HouseholdGoal;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HouseholdGoalRepository extends JpaRepository<HouseholdGoal, Long> {
  @EntityGraph(attributePaths = {"goalTemplate", "goalTemplate.goalCategory"})
  List<HouseholdGoal> findByHouseholdId(Long householdId);

}
