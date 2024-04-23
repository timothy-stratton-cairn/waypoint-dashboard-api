package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.entity.HomeworkResponse;
import com.cairn.waypoint.dashboard.repository.HomeworkResponseRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class HomeworkResponseDataService {

  private final HomeworkResponseRepository homeworkResponseRepository;

  public HomeworkResponseDataService(HomeworkResponseRepository homeworkResponseRepository) {
    this.homeworkResponseRepository = homeworkResponseRepository;
  }

  public Optional<HomeworkResponse> getHomeworkResponseByFileGuid(String fileGuid) {
    return this.homeworkResponseRepository.findHomeworkResponseByFileGuid(fileGuid);
  }

}
