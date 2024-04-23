package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.entity.HomeworkResponse;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeworkResponseRepository extends JpaRepository<HomeworkResponse, Long> {

  Optional<HomeworkResponse> findHomeworkResponseByFileGuid(String fileGuid);
}
