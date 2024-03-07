package com.cairn.waypoint.dashboard.service;


import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolStep;
import com.cairn.waypoint.dashboard.entity.ProtocolUser;
import com.cairn.waypoint.dashboard.repository.ProtocolRepository;
import com.cairn.waypoint.dashboard.repository.ProtocolStepRepository;
import com.cairn.waypoint.dashboard.repository.ProtocolUserRepository;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ProtocolService {

  private final ProtocolRepository protocolRepository;
  private final ProtocolStepRepository protocolStepRepository;
  private final ProtocolUserRepository protocolUserRepository;

  public ProtocolService(ProtocolRepository protocolRepository,
      ProtocolStepRepository protocolStepRepository,
      ProtocolUserRepository protocolUserRepository) {
    this.protocolRepository = protocolRepository;
    this.protocolStepRepository = protocolStepRepository;
    this.protocolUserRepository = protocolUserRepository;
  }

  public List<Protocol> getAllProtocols() {
    return this.protocolRepository.findAll();
  }

  public Optional<Protocol> getProtocolById(Long id) {
    return this.protocolRepository.findById(id);
  }

  public Optional<Protocol> getByProtocolTemplateIdAndUserId(Long protocolTemplateId, Long userId) {
    return this.protocolRepository.findByAssociatedUsers_UserId(userId).stream()
        .filter(protocol -> protocol.getProtocolTemplate()
            .getId()
            .equals(protocolTemplateId))
        .findFirst();
  }

  @Transactional
  public Protocol saveProtocol(Protocol protocolToBeCreated) {
    //Save the Protocol
    Set<ProtocolStep> protocolSteps = protocolToBeCreated.getProtocolSteps();
    protocolToBeCreated.setProtocolSteps(null);
    Set<ProtocolUser> protocolUsers = protocolToBeCreated.getAssociatedUsers();
    protocolToBeCreated.setAssociatedUsers(null);

    Protocol createdProtocol = this.protocolRepository.save(protocolToBeCreated);

    //Save the Protocol Steps
    Set<ProtocolStep> createdProtocolSteps = protocolSteps.stream()
        .peek(protocolStep -> protocolStep.setParentProtocol(createdProtocol))
        .map(this.protocolStepRepository::saveAndFlush)
        .collect(Collectors.toCollection(LinkedHashSet::new));
    createdProtocol.setProtocolSteps(createdProtocolSteps);

    //Save the Protocol-User associations
    Set<ProtocolUser> createdProtocolUsers = protocolUsers.stream()
        .peek(protocolUser -> protocolUser.setProtocol(createdProtocol))
        .map(this.protocolUserRepository::saveAndFlush)
        .collect(Collectors.toCollection(LinkedHashSet::new));
    createdProtocol.setAssociatedUsers(createdProtocolUsers);

    //Safeguard for any hanging entities
    return this.protocolRepository.saveAndFlush(createdProtocol);
  }
}
