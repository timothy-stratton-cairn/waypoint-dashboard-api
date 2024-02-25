package com.cairn.waypoint.dashboard.service;


import com.cairn.waypoint.dashboard.entity.StepTemplate;
import com.cairn.waypoint.dashboard.repository.StepTemplateRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class StepTemplateService {

  private final StepTemplateRepository stepTemplateRepository;

  public StepTemplateService(StepTemplateRepository stepTemplateRepository) {
    this.stepTemplateRepository = stepTemplateRepository;
  }

  public Set<StepTemplate> getStepTemplateEntitiesFromIdCollection(Set<Long> roleIdList) {
    return roleIdList.stream()
        .map(roleId -> this.stepTemplateRepository.findById(roleId)
            .orElseThrow(
                () -> new EntityNotFoundException(
                    "Step Template with ID [" + roleId + "] not found")))
        .collect(Collectors.toSet());
  }
}
