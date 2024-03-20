package com.cairn.waypoint.dashboard.service;

import com.cairn.waypoint.dashboard.entity.ProtocolStep;
import com.cairn.waypoint.dashboard.repository.ProtocolStepRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ProtocolStepService {
  private final ProtocolStepRepository protocolStepRepository;
  public ProtocolStepService(ProtocolStepRepository protocolStepRepository) {
    this.protocolStepRepository = protocolStepRepository;
  }

  public Long saveProtocolStep(ProtocolStep protocolStep) {
    return protocolStepRepository.save(protocolStep).getId();
  }
}
