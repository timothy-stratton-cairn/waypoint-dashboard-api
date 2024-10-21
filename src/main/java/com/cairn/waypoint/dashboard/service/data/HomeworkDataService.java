package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.entity.Homework;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.HomeworkTemplate;
import com.cairn.waypoint.dashboard.repository.HomeworkRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class HomeworkDataService {

  private final HomeworkRepository homeworkRepository;

  public HomeworkDataService(HomeworkRepository homeworkRepository) {
    this.homeworkRepository = homeworkRepository;
  }

  public Homework saveHomework(Homework homework) {
    return this.homeworkRepository.save(homework);
  }

  public Optional<Homework> getHomeworkById(Long id) {
    return this.homeworkRepository.findById(id);
  }

  public List<Homework> getHomeworkByAssignedHouseholdId(Long assignedHouseholdId) {
    return this.homeworkRepository.findByAssignedHouseholdId(assignedHouseholdId);
  }

  public List<Homework> getHomeworkByHomeworkTemplate(HomeworkTemplate homeworkTemplate) {
    return this.homeworkRepository.findByHomeworkTemplate(homeworkTemplate);
  }
  
}
