package com.cairn.waypoint.dashboard.service;


import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.repository.ProtocolTemplateRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ProtocolTemplateService {

  private final ProtocolTemplateRepository protocolTemplateRepository;

  public ProtocolTemplateService(ProtocolTemplateRepository protocolTemplateRepository) {
    this.protocolTemplateRepository = protocolTemplateRepository;
  }

  public List<ProtocolTemplate> getAllProtocolTemplates() {
    return this.protocolTemplateRepository.findAll();
  }

  public Optional<ProtocolTemplate> getProtocolTemplateById(Long id) {
    return this.protocolTemplateRepository.findById(id);
  }

  public Long saveProtocolTemplate(ProtocolTemplate protocolTemplate) {
    return this.protocolTemplateRepository.save(protocolTemplate).getId();
  }

  public Optional<ProtocolTemplate> findProtocolTemplateByName(String name) {
    return this.protocolTemplateRepository.findByName(name);
  }
}
