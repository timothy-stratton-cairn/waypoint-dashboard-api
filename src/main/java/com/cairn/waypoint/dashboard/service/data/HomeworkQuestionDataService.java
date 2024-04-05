package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.repository.HomeworkQuestionRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class HomeworkQuestionDataService {

  private final HomeworkQuestionRepository homeworkQuestionRepository;

  public HomeworkQuestionDataService(HomeworkQuestionRepository homeworkQuestionRepository) {
    this.homeworkQuestionRepository = homeworkQuestionRepository;
  }

  public List<HomeworkQuestion> getAllHomeworkQuestions() {
    return this.homeworkQuestionRepository.findAll();
  }

  public Optional<HomeworkQuestion> getHomeworkQuestionById(Long id) {
    return this.homeworkQuestionRepository.findById(id);
  }

  public HomeworkQuestion saveHomeworkQuestion(HomeworkQuestion homeworkQuestion) {
    return this.homeworkQuestionRepository.save(homeworkQuestion);
  }
}
