package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.entity.GoalTemplate;
import com.cairn.waypoint.dashboard.entity.HouseholdLinkedProtocolGoalTemplates;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.repository.HouseholdGoalsLinkedProtocolTemplateRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class HouseholdGoalsLinkedProtocolTemplateDataService {
  private final HouseholdGoalsLinkedProtocolTemplateRepository goalLinkedProtocolRepository;

  public HouseholdGoalsLinkedProtocolTemplateDataService(HouseholdGoalsLinkedProtocolTemplateRepository goalLinkedProtocolRepository) {
    this.goalLinkedProtocolRepository = goalLinkedProtocolRepository;
  }

  public boolean linkExists(Long protocolTemplateId, Long goalTemplateId) {
    return goalLinkedProtocolRepository.existsByProtocolTemplateIdAndGoalTemplateId(protocolTemplateId, goalTemplateId);
  }

  public HouseholdLinkedProtocolGoalTemplates createLink(Long protocolTemplateId, Long goalTemplateId) {
    HouseholdLinkedProtocolGoalTemplates newLink = HouseholdLinkedProtocolGoalTemplates.builder()
        .protocolTemplate(ProtocolTemplate.builder().id(protocolTemplateId).build())
        .goalTemplate(GoalTemplate.builder().id(goalTemplateId).build())
        .build();

    return goalLinkedProtocolRepository.save(newLink);
  }



  public List<HouseholdLinkedProtocolGoalTemplates> getAllHouseholdLinkedProtocolGoalTemplates() {
    return goalLinkedProtocolRepository.findAll();
  }

  public Optional<HouseholdLinkedProtocolGoalTemplates> getHouseholdLinkedProtocolGoalTemplateById(Long id) {
    return goalLinkedProtocolRepository.findById(id);
  }

  public HouseholdLinkedProtocolGoalTemplates saveHouseholdLinkedProtocolGoalTemplate(HouseholdLinkedProtocolGoalTemplates householdLinkedProtocolGoalTemplate) {
    return goalLinkedProtocolRepository.save(householdLinkedProtocolGoalTemplate);
  }

  public void deleteHouseholdLinkedProtocolGoalTemplateById(Long id) {
    goalLinkedProtocolRepository.deleteById(id);
  }
}
