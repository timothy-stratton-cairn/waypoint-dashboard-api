package com.cairn.waypoint.dashboard.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestionLinkedProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;

public interface HomeworkQuestionLinkedProtocolTemplatesRepository extends JpaRepository< 
HomeworkQuestionLinkedProtocolTemplate, Long> {

	HomeworkQuestionLinkedProtocolTemplate findByQuestion_Id(Long homeworkId);
	List<HomeworkQuestionLinkedProtocolTemplate> findAllByQuestion_Id(Long questionId);
    List<HomeworkQuestionLinkedProtocolTemplate> findByProtocolTemplate_Id(Long templateId);

}
