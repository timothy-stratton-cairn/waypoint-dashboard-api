package com.cairn.waypoint.dashboard.service;

import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.repository.HomeworkQuestionRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class HomeworkQuestionService {

  private final HomeworkQuestionRepository homeworkQuestionRepository;

  public HomeworkQuestionService(HomeworkQuestionRepository homeworkQuestionRepository) {
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
