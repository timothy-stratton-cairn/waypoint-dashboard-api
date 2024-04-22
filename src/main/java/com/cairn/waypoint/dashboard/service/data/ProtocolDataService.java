package com.cairn.waypoint.dashboard.service.data;


import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolCommentary;
import com.cairn.waypoint.dashboard.entity.ProtocolStep;
import com.cairn.waypoint.dashboard.entity.ProtocolUser;
import com.cairn.waypoint.dashboard.repository.ProtocolCommentaryRepository;
import com.cairn.waypoint.dashboard.repository.ProtocolRepository;
import com.cairn.waypoint.dashboard.repository.ProtocolStepRepository;
import com.cairn.waypoint.dashboard.repository.ProtocolUserRepository;
import jakarta.transaction.Transactional;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ProtocolDataService {

  private final ProtocolRepository protocolRepository;
  private final ProtocolStepRepository protocolStepRepository;
  private final ProtocolUserRepository protocolUserRepository;
  private final ProtocolCommentaryRepository protocolCommentaryRepository;

  public ProtocolDataService(ProtocolRepository protocolRepository,
      ProtocolStepRepository protocolStepRepository,
      ProtocolUserRepository protocolUserRepository,
      ProtocolCommentaryRepository protocolCommentaryRepository) {
    this.protocolRepository = protocolRepository;
    this.protocolStepRepository = protocolStepRepository;
    this.protocolUserRepository = protocolUserRepository;
    this.protocolCommentaryRepository = protocolCommentaryRepository;
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

  public List<Protocol> getByProtocolTemplateId(Long protocolTemplateId) {
    return this.protocolRepository.findByProtocolTemplate_Id(protocolTemplateId);
  }

  public List<Protocol> getByUserId(Long userId) {
    return this.protocolRepository.findByAssociatedUsers_UserId(userId);
  }

  @Transactional
  public Protocol saveProtocol(Protocol protocolToBeCreated) {
    //Save the Protocol
    Set<ProtocolStep> protocolSteps = protocolToBeCreated.getProtocolSteps();
    protocolToBeCreated.setProtocolSteps(null);
    Set<ProtocolUser> protocolUsers = protocolToBeCreated.getAssociatedUsers();
    protocolToBeCreated.setAssociatedUsers(null);
    Set<ProtocolCommentary> protocolComments = protocolToBeCreated.getComments();
    protocolToBeCreated.setComments(null);

    Protocol createdProtocol = this.protocolRepository.save(protocolToBeCreated);

    //Save the Protocol Steps
    protocolSteps
        .forEach(protocolStep -> protocolStep.setParentProtocol(createdProtocol));
    Set<ProtocolStep> createdProtocolSteps = protocolSteps.stream()
        .map(this.protocolStepRepository::save)
        .collect(Collectors.toCollection(LinkedHashSet::new));
    createdProtocol.setProtocolSteps(createdProtocolSteps);

    //Save the Protocol-User associations
    protocolUsers
        .forEach(protocolUser -> protocolUser.setProtocol(createdProtocol));
    Set<ProtocolUser> createdProtocolUsers = protocolUsers.stream()
        .map(this.protocolUserRepository::save)
        .collect(Collectors.toCollection(LinkedHashSet::new));
    createdProtocol.setAssociatedUsers(createdProtocolUsers);

    //Save the Protocol Commentary
    protocolComments
        .forEach(protocolComment -> protocolComment.setProtocol(createdProtocol));
    Set<ProtocolCommentary> createdProtocolComments = protocolComments.stream()
        .map(this.protocolCommentaryRepository::save)
        .collect(Collectors.toCollection(LinkedHashSet::new));
    createdProtocol.setComments(createdProtocolComments);

    //Safeguard for any hanging entities
    return this.protocolRepository.save(createdProtocol);
  }

  public Protocol updateProtocol(Protocol protocolToBeUpdated) {
    return this.protocolRepository.save(protocolToBeUpdated);
  }
}
