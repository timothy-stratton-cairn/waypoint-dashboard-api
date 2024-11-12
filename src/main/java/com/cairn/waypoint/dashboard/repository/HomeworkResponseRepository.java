package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.HomeworkResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeworkResponseRepository extends JpaRepository<HomeworkResponse, Long> {

  Optional<HomeworkResponse> findHomeworkResponseByFileGuid(String fileGuid);

  List<HomeworkResponse> findHomeworkResponseByHomeworkQuestion(HomeworkQuestion homeworkQuestion);

  List<HomeworkResponse> getHomeworkResponseByCategory_Id(Long categoryId);

  List<HomeworkResponse> findByHomeworkQuestion_IdIn(List<Long> questionIds);

  <Optional> HomeworkResponse findByHomeworkQuestion(HomeworkQuestion homeworkQuestion);

  List<HomeworkResponse> findAllByUserId(Long userId);
}
