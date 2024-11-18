package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.entity.ExpectedResponse;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpectedResponseRepository extends JpaRepository<ExpectedResponse, Long> {

  Optional<ExpectedResponse> findExpectedResponseByResponse(String response);

}
