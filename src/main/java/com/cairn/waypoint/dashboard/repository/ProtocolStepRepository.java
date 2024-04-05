package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.entity.ProtocolStep;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProtocolStepRepository extends JpaRepository<ProtocolStep, Long> {
  List<ProtocolStep> findByTemplate_Id(Long stepTemplateId);
}
