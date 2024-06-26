package com.cairn.waypoint.dashboard.service.data;


import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.repository.ProtocolTemplateRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ProtocolTemplateDataService {

  private final ProtocolTemplateRepository protocolTemplateRepository;

  public ProtocolTemplateDataService(ProtocolTemplateRepository protocolTemplateRepository) {
    this.protocolTemplateRepository = protocolTemplateRepository;
  }

  public List<ProtocolTemplate> getAllProtocolTemplates() {
    return this.protocolTemplateRepository.findAll();
  }

  public Optional<ProtocolTemplate> getProtocolTemplateById(Long id) {
    return this.protocolTemplateRepository.findById(id);
  }

  public ProtocolTemplate saveProtocolTemplate(ProtocolTemplate protocolTemplate) {
    return this.protocolTemplateRepository.save(protocolTemplate);
  }

  public Optional<ProtocolTemplate> findProtocolTemplateByName(String name) {
    return this.protocolTemplateRepository.findByName(name);
  }

  public List<ProtocolTemplate> saveProtocolTemplateList(List<ProtocolTemplate> protocolTemplates) {
    return this.protocolTemplateRepository.saveAllAndFlush(protocolTemplates);
  }
}
