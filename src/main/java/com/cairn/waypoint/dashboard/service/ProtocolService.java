package com.cairn.waypoint.dashboard.service;


import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolStep;
import com.cairn.waypoint.dashboard.entity.ProtocolUser;
import com.cairn.waypoint.dashboard.repository.ProtocolRepository;
import com.cairn.waypoint.dashboard.repository.ProtocolStepRepository;
import com.cairn.waypoint.dashboard.repository.ProtocolUserRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
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

  public Protocol saveProtocol(Protocol protocolToBeCreated) {
    //Save the Protocol
    Set<ProtocolStep> protocolSteps = protocolToBeCreated.getProtocolSteps();
    protocolToBeCreated.setProtocolSteps(null);
    Set<ProtocolUser> protocolUsers = protocolToBeCreated.getAssociatedUsers();
    protocolToBeCreated.setAssociatedUsers(null);

    Protocol createdProtocol = this.protocolRepository.save(protocolToBeCreated);

    //Save the Protocol Steps
    protocolSteps.forEach(protocolStep -> protocolStep.setParentProtocol(createdProtocol));

    Set<ProtocolStep> createdProtocolSteps = new HashSet<>(
        this.protocolStepRepository.saveAll(protocolSteps));
    createdProtocol.setProtocolSteps(createdProtocolSteps);

    //Save the Protocol User associations
    protocolUsers.forEach(protocolUser -> protocolUser.setProtocol(createdProtocol));

    Set<ProtocolUser> createdProtocolUsers = new HashSet<>(
        this.protocolUserRepository.saveAll(protocolUsers));
    protocolToBeCreated.setAssociatedUsers(createdProtocolUsers);

    //Safeguard for any hanging entities
    return this.protocolRepository.saveAndFlush(createdProtocol);
  }
}
