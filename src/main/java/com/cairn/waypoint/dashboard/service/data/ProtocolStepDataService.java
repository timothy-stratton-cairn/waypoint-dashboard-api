package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.entity.ProtocolStep;
import com.cairn.waypoint.dashboard.repository.ProtocolStepRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ProtocolStepDataService {

  private final ProtocolStepRepository protocolStepRepository;

  public ProtocolStepDataService(ProtocolStepRepository protocolStepRepository) {
    this.protocolStepRepository = protocolStepRepository;
  }

  public ProtocolStep saveProtocolStep(ProtocolStep protocolStep) {
    return protocolStepRepository.save(protocolStep);
  }

  public Optional<ProtocolStep> getProtocolStepById(Long protocolStepId) {
    return this.protocolStepRepository.findById(protocolStepId);
  }

  public List<ProtocolStep> getProtocolStepsByStepTemplateId(Long protocolStepTemplateId) {
    return this.protocolStepRepository.findByTemplate_Id(protocolStepTemplateId);
  }

  public List<ProtocolStep> saveProtocolStepList(List<ProtocolStep> protocolSteps) {
    return this.protocolStepRepository.saveAllAndFlush(protocolSteps);
  }
}
