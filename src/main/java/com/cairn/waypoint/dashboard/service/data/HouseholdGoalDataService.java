package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.entity.HouseholdGoal;
import com.cairn.waypoint.dashboard.repository.HouseholdGoalRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class HouseholdGoalDataService {
  private final HouseholdGoalRepository repository;

  public HouseholdGoalDataService(HouseholdGoalRepository repository) {
    this.repository = repository;
  }
  public List<HouseholdGoal> getGoalsByHouseholdId(Long householdId) {
    return repository.findByHouseholdId(householdId);
  }

  public List<HouseholdGoal> getAllGoals() {
    return repository.findAll();
  }

  public HouseholdGoal createGoal(HouseholdGoal householdGoal) {
    return repository.save(householdGoal);
  }

}
