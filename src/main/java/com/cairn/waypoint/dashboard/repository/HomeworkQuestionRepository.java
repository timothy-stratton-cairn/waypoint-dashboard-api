package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeworkQuestionRepository extends JpaRepository<HomeworkQuestion, Long> {

  Optional<HomeworkQuestion> findByQuestion(String question);

  List<HomeworkQuestion> findByTriggeredProtocol(ProtocolTemplate protocolTemplate);
 

  List<HomeworkQuestion> getHomeworkQuestionByCategory_Id(Long categoryId);

}
