package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.entity.Homework;
import com.cairn.waypoint.dashboard.repository.HomeworkRepository;
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
}
