package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.entity.Protocol;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProtocolRepository extends JpaRepository<Protocol, Long> {

  List<Protocol> findByProtocolTemplate_Id(Long protocolTemplateId);

  List<Protocol> findByAssignedHouseholdId(Long householdId);
}
