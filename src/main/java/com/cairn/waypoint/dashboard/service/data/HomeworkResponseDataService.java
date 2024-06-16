package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.HomeworkResponse;
import com.cairn.waypoint.dashboard.repository.HomeworkResponseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class HomeworkResponseDataService {

  private final HomeworkResponseRepository homeworkResponseRepository;

  public HomeworkResponseDataService(HomeworkResponseRepository homeworkResponseRepository) {
    this.homeworkResponseRepository = homeworkResponseRepository;
  }

  public HomeworkResponse saveHomeworkResponse(HomeworkResponse homeworkResponse) {
    return this.homeworkResponseRepository.save(homeworkResponse);
  }

  public Optional<HomeworkResponse> getHomeworkResponseByFileGuid(String fileGuid) {
    return this.homeworkResponseRepository.findHomeworkResponseByFileGuid(fileGuid);
  }

  public List<HomeworkResponse> getAllHomeworkResponsesByHomeworkQuestion(
      HomeworkQuestion homeworkQuestion) {
    return this.homeworkResponseRepository.findHomeworkResponseByHomeworkQuestion(homeworkQuestion);
  }
}
