package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.entity.HouseholdLinkedProtocolGoalTemplates;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HouseholdGoalsLinkedProtocolTemplateRepository extends JpaRepository<HouseholdLinkedProtocolGoalTemplates, Long> {

  boolean existsByProtocolTemplateIdAndGoalTemplateId(Long protocolTemplateId, Long goalTemplateId);

}
