package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.HomeworkTemplateLinkedHomeworkQuestion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeworkTemplateLinkedHomeworkQuestionRepository extends
    JpaRepository<HomeworkTemplateLinkedHomeworkQuestion, Long> {

  List<HomeworkTemplateLinkedHomeworkQuestion> findByHomeworkQuestion(
      HomeworkQuestion homeworkQuestion);
}
