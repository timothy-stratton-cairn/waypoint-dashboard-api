package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.HomeworkResponse;
import java.util.List;
import java.util.Optional;

import com.cairn.waypoint.dashboard.entity.Protocol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeworkResponseRepository extends JpaRepository<HomeworkResponse, Long> {

  Optional<HomeworkResponse> findHomeworkResponseByFileGuid(String fileGuid);
  

  List<HomeworkResponse> findHomeworkResponseByHomeworkQuestion(HomeworkQuestion homeworkQuestion);

  List<HomeworkResponse> getHomeworkResponseByCategory_Id(Long categoryId);
  
  List<HomeworkResponse> findByHomeworkQuestion_IdIn(List<Long> questionIds);

  List<HomeworkResponse> findByProtocolAndHomeworkQuestion(Protocol protocol, HomeworkQuestion question);

  List<HomeworkResponse> findByProtocol_Id(Long protocolId);

  Optional<HomeworkResponse> findByHomeworkQuestion_IdAndProtocol_Id(Long questionId, Long protocolId);
  
 
}
