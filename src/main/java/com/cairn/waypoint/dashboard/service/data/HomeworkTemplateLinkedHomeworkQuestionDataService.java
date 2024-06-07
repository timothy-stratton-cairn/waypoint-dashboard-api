package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.entity.HomeworkTemplateLinkedHomeworkQuestion;
import com.cairn.waypoint.dashboard.repository.HomeworkTemplateLinkedHomeworkQuestionRepository;
import org.springframework.stereotype.Service;

@Service
public class HomeworkTemplateLinkedHomeworkQuestionDataService {

  private final HomeworkTemplateLinkedHomeworkQuestionRepository homeworkTemplateLinkedHomeworkQuestionRepository;

  public HomeworkTemplateLinkedHomeworkQuestionDataService(
      HomeworkTemplateLinkedHomeworkQuestionRepository homeworkTemplateLinkedHomeworkQuestionRepository) {
    this.homeworkTemplateLinkedHomeworkQuestionRepository = homeworkTemplateLinkedHomeworkQuestionRepository;
  }

  public HomeworkTemplateLinkedHomeworkQuestion saveHomeworkTemplateLinkedHomeworkQuestion(
      HomeworkTemplateLinkedHomeworkQuestion homeworkTemplateLinkedHomeworkQuestion) {
    return this.homeworkTemplateLinkedHomeworkQuestionRepository.save(
        homeworkTemplateLinkedHomeworkQuestion);
  }
}
