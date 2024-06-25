package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.entity.ProtocolTemplateLinkedStepTemplate;
import com.cairn.waypoint.dashboard.entity.StepTemplate;
import com.cairn.waypoint.dashboard.repository.ProtocolTemplateLinkedStepTemplateRepository;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProtocolTemplateLinkedStepTemplateDataService {

  private final ProtocolTemplateLinkedStepTemplateRepository protocolTemplateLinkedStepTemplateRepository;

  public ProtocolTemplateLinkedStepTemplateDataService(
      ProtocolTemplateLinkedStepTemplateRepository protocolTemplateLinkedStepTemplateRepository) {
    this.protocolTemplateLinkedStepTemplateRepository = protocolTemplateLinkedStepTemplateRepository;
  }

  public void deleteCollectionOfProtocolTemplateLinkedStepTemplates(
      Collection<ProtocolTemplateLinkedStepTemplate> protocolTemplateLinkedStepTemplates) {
    protocolTemplateLinkedStepTemplates.forEach(
        protocolTemplateLinkedStepTemplate -> protocolTemplateLinkedStepTemplate.setActive(
            Boolean.FALSE));

    this.protocolTemplateLinkedStepTemplateRepository.saveAll(
        protocolTemplateLinkedStepTemplates);
  }

  public ProtocolTemplateLinkedStepTemplate saveProtocolTemplateLinkedStepTemplate(ProtocolTemplateLinkedStepTemplate protocolTemplateLinkedStepTemplate) {
    return this.protocolTemplateLinkedStepTemplateRepository.save(protocolTemplateLinkedStepTemplate);
  }

  public List<ProtocolTemplateLinkedStepTemplate> getProtocolTemplateLinkedStepTemplateByStepTemplate(
      StepTemplate stepTemplate) {
    return this.protocolTemplateLinkedStepTemplateRepository.getProtocolTemplateLinkedStepTemplateByStepTemplate(stepTemplate);
  }

}
