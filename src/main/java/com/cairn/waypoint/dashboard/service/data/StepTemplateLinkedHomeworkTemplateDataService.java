package com.cairn.waypoint.dashboard.service.data;


import com.cairn.waypoint.dashboard.entity.HomeworkTemplate;
import com.cairn.waypoint.dashboard.entity.StepTemplateLinkedHomeworkTemplate;
import com.cairn.waypoint.dashboard.repository.StepTemplateLinkedHomeworkTemplateRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StepTemplateLinkedHomeworkTemplateDataService {

  private final StepTemplateLinkedHomeworkTemplateRepository stepTemplateLinkedHomeworkTemplateRepository;

  public StepTemplateLinkedHomeworkTemplateDataService(
      StepTemplateLinkedHomeworkTemplateRepository stepTemplateLinkedHomeworkTemplateRepository) {
    this.stepTemplateLinkedHomeworkTemplateRepository = stepTemplateLinkedHomeworkTemplateRepository;
  }

  public List<StepTemplateLinkedHomeworkTemplate> saveAll(
      List<StepTemplateLinkedHomeworkTemplate> stepTemplateLinkedHomeworkTemplates) {
    return this.stepTemplateLinkedHomeworkTemplateRepository.saveAll(
        stepTemplateLinkedHomeworkTemplates);
  }

  public List<StepTemplateLinkedHomeworkTemplate> findAllByHomeworkTemplate(
      HomeworkTemplate homeworkTemplate) {
    return this.stepTemplateLinkedHomeworkTemplateRepository.findByHomeworkTemplate(
        homeworkTemplate);
  }
}
