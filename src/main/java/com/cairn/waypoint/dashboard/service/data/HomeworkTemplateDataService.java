package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.entity.HomeworkTemplate;
import com.cairn.waypoint.dashboard.repository.HomeworkTemplateRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class HomeworkTemplateDataService {

  private final HomeworkTemplateRepository homeworkTemplateRepository;

  public HomeworkTemplateDataService(HomeworkTemplateRepository homeworkTemplateRepository) {
    this.homeworkTemplateRepository = homeworkTemplateRepository;
  }

  public List<HomeworkTemplate> getAllHomeworkTemplates() {
    return this.homeworkTemplateRepository.findAll();
  }

  public Optional<HomeworkTemplate> getHomeworkTemplateById(Long id) {
    return this.homeworkTemplateRepository.findById(id);
  }

  public List<HomeworkTemplate> getHomeworkTemplates(List<Long> ids) {
    return this.homeworkTemplateRepository.findAllById(ids);
  }

  public HomeworkTemplate saveHomeworkTemplate(HomeworkTemplate homeworkTemplate) {
    return this.homeworkTemplateRepository.save(homeworkTemplate);
  }

  public Optional<HomeworkTemplate> findHomeworkTemplateByName(String name) {
    return this.homeworkTemplateRepository.findByName(name);
  }
}
