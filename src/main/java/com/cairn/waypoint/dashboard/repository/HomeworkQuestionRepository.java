package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeworkQuestionRepository extends JpaRepository<HomeworkQuestion, Long> {

  List<HomeworkQuestion> findByTriggeredProtocol(ProtocolTemplate protocolTemplate);

}
