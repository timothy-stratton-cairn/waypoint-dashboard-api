package com.cairn.waypoint.dashboard.service;

import com.cairn.waypoint.dashboard.entity.ProtocolTemplateLinkedStepTemplate;
import com.cairn.waypoint.dashboard.repository.ProtocolTemplateLinkedStepTemplateRepository;
import java.util.Collection;
import org.springframework.stereotype.Service;

@Service
public class ProtocolTemplateLinkedStepTemplateService {

  private final ProtocolTemplateLinkedStepTemplateRepository protocolTemplateLinkedStepTemplateRepository;

  public ProtocolTemplateLinkedStepTemplateService(
      ProtocolTemplateLinkedStepTemplateRepository protocolTemplateLinkedStepTemplateRepository) {
    this.protocolTemplateLinkedStepTemplateRepository = protocolTemplateLinkedStepTemplateRepository;
  }

  public void deleteCollectionOfProtocolTemplateLinkedStepTemplates(
      Collection<ProtocolTemplateLinkedStepTemplate> protocolTemplateLinkedStepTemplates) {
    protocolTemplateLinkedStepTemplates.forEach(
        protocolTemplateLinkedStepTemplate -> protocolTemplateLinkedStepTemplate.setActive(
            Boolean.FALSE));

    this.protocolTemplateLinkedStepTemplateRepository.saveAllAndFlush(
        protocolTemplateLinkedStepTemplates);
  }

}
