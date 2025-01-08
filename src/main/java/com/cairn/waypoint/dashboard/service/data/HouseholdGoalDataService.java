package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.entity.HouseholdGoal;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.repository.HouseholdGoalRepository;
import com.cairn.waypoint.dashboard.entity.HouseholdLinkedProtocolGoalTemplates;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class HouseholdGoalDataService {
  private final HouseholdGoalRepository repository;
  private final HouseholdGoalsLinkedProtocolTemplateDataService linkedProtocolService;

  public HouseholdGoalDataService(HouseholdGoalRepository repository,
      HouseholdGoalsLinkedProtocolTemplateDataService linkedProtocolService) {
    this.repository = repository;
    this.linkedProtocolService = linkedProtocolService;
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

  public List<ProtocolTemplate> getProtocolTemplatesForGoalTemplate(Long goalTemplateId) {
    return linkedProtocolService.getAllHouseholdLinkedProtocolGoalTemplates().stream()
        .filter(link -> link.getGoalTemplate().getId().equals(goalTemplateId))
        .map(HouseholdLinkedProtocolGoalTemplates::getProtocolTemplate)
        .collect(Collectors.toList());
  }

}
