package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.entity.Homework;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.HomeworkTemplate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeworkRepository extends JpaRepository<Homework, Long> {

  List<Homework> findByAssignedHouseholdId(Long id);

  List<Homework> findByHomeworkTemplate(HomeworkTemplate homeworkTemplate);

}
