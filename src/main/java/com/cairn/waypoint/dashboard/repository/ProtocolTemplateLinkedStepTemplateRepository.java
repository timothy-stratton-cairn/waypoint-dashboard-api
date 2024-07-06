package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.entity.ProtocolTemplateLinkedStepTemplate;
import com.cairn.waypoint.dashboard.entity.StepTemplate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProtocolTemplateLinkedStepTemplateRepository extends
    JpaRepository<ProtocolTemplateLinkedStepTemplate, Long> {

  List<ProtocolTemplateLinkedStepTemplate> getProtocolTemplateLinkedStepTemplateByStepTemplate(
      StepTemplate stepTemplate);
}
