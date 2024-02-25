package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProtocolTemplateRepository extends JpaRepository<ProtocolTemplate, Long> {

  Optional<ProtocolTemplate> findByName(String name);
}
