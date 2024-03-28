package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.entity.ExpectedResponse;
import com.cairn.waypoint.dashboard.entity.Homework;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpectedResponseRepository extends JpaRepository<ExpectedResponse, Long> {

}
