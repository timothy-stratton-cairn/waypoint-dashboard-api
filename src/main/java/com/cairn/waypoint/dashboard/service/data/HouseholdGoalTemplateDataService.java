package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.entity.GoalTemplate;
import com.cairn.waypoint.dashboard.repository.HouseholdGoalsTemplateRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class HouseholdGoalTemplateDataService {
  private final HouseholdGoalsTemplateRepository goalRepository;

  public HouseholdGoalTemplateDataService(HouseholdGoalsTemplateRepository goalRepository) {
    this.goalRepository = goalRepository;
  }

  public List<GoalTemplate> getAllGoalTemplates() {
    return goalRepository.findAll();
  }

  public Optional<GoalTemplate> getGoalTemplateById(Long id) {
    return goalRepository.findById(id);
  }

  public GoalTemplate createGoalTemplate(GoalTemplate goalTemplate) {
    return goalRepository.save(goalTemplate);
  }

  public GoalTemplate saveGoalTemplate(GoalTemplate goalTemplate) {
    return goalRepository.save(goalTemplate);
  }

  public void deleteGoalTemplateById(Long id) {
    goalRepository.deleteById(id);
  }
}
