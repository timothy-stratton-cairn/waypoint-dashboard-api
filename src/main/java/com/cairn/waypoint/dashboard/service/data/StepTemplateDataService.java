package com.cairn.waypoint.dashboard.service.data;


import com.cairn.waypoint.dashboard.entity.StepTemplate;
import com.cairn.waypoint.dashboard.repository.StepTemplateRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class StepTemplateDataService {

  private final StepTemplateRepository stepTemplateRepository;

  public StepTemplateDataService(StepTemplateRepository stepTemplateRepository) {
    this.stepTemplateRepository = stepTemplateRepository;
  }

  public List<StepTemplate> getAllStepTemplates() {
    return this.stepTemplateRepository.findAll();
  }

  public Optional<StepTemplate> getStepTemplateById(Long id) {
    return this.stepTemplateRepository.findById(id);
  }

  public StepTemplate saveStepTemplate(StepTemplate stepTemplate) {
    return this.stepTemplateRepository.save(stepTemplate);
  }

  public Optional<StepTemplate> findStepTemplateByName(String name) {
    return this.stepTemplateRepository.findByName(name);
  }

  public LinkedHashSet<StepTemplate> getStepTemplateEntitiesFromIdCollection(
      Set<Long> stepTemplateIdList) {
    return stepTemplateIdList.stream()
        .map(stepTemplateId -> this.stepTemplateRepository.findById(stepTemplateId)
            .orElseThrow(
                () -> new EntityNotFoundException(
                    "Step Template with ID [" + stepTemplateId + "] not found")))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public List<StepTemplate> saveStepTemplateList(List<StepTemplate> stepTemplates) {
    return this.stepTemplateRepository.saveAllAndFlush(stepTemplates);
  }

  public List<StepTemplate> getAllStepTemplatesByTemplateCategoryId(Long templateCategoryId) {
    return this.stepTemplateRepository.findByCategory_Id(templateCategoryId);
  }
}
