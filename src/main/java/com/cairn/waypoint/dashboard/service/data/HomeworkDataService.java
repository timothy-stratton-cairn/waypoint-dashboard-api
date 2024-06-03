package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.entity.Homework;
import com.cairn.waypoint.dashboard.repository.HomeworkRepository;
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

  public Optional<Homework> getHomeworkByAssignedHouseholdId(Long assignedHouseholdId) {
    return this.homeworkRepository.findByAssignedHouseholdId(assignedHouseholdId);
  }
}
