package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.entity.HomeworkTemplate;
import com.cairn.waypoint.dashboard.entity.StepTemplateLinkedHomeworkTemplate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StepTemplateLinkedHomeworkTemplateRepository extends
    JpaRepository<StepTemplateLinkedHomeworkTemplate, Long> {

  List<StepTemplateLinkedHomeworkTemplate> findByHomeworkTemplate(
      HomeworkTemplate homeworkTemplate);
}
