package com.cairn.waypoint.dashboard.service;

import com.cairn.waypoint.dashboard.entity.HomeworkTemplate;
import com.cairn.waypoint.dashboard.repository.HomeworkTemplateRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class HomeworkTemplateService {

  private final HomeworkTemplateRepository homeworkTemplateRepository;

  public HomeworkTemplateService(HomeworkTemplateRepository homeworkTemplateRepository) {
    this.homeworkTemplateRepository = homeworkTemplateRepository;
  }

  public List<HomeworkTemplate> getAllHomeworkTemplates() {
    return this.homeworkTemplateRepository.findAll();
  }

  public Optional<HomeworkTemplate> getHomeworkTemplateById(Long id) {
    return this.homeworkTemplateRepository.findById(id);
  }

  public Long saveHomeworkTemplate(HomeworkTemplate homeworkTemplate) {
    return this.homeworkTemplateRepository.save(homeworkTemplate).getId();
  }

  public Optional<HomeworkTemplate> findHomeworkTemplateByName(String name) {
    return this.homeworkTemplateRepository.findByName(name);
  }
}
