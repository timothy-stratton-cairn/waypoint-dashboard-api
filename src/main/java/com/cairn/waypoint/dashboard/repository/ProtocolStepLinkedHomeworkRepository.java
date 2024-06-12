package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.entity.Homework;
import com.cairn.waypoint.dashboard.entity.HomeworkTemplateLinkedHomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.ProtocolStepLinkedHomework;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProtocolStepLinkedHomeworkRepository extends
    JpaRepository<ProtocolStepLinkedHomework, Long> {
  List<ProtocolStepLinkedHomework> findByHomework(Homework homework);
}
