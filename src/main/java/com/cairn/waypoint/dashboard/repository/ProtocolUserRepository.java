package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.entity.ProtocolUser;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProtocolUserRepository extends JpaRepository<ProtocolUser, Long> {

  List<ProtocolUser> findByUserId(Long userId);
}
