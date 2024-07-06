package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolCommentary;
import com.cairn.waypoint.dashboard.entity.ProtocolStep;
import com.cairn.waypoint.dashboard.entity.enumeration.RecurrenceTypeEnum;
import com.cairn.waypoint.dashboard.repository.ProtocolCommentaryRepository;
import com.cairn.waypoint.dashboard.repository.ProtocolRepository;
import com.cairn.waypoint.dashboard.repository.ProtocolStepRepository;
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
  private final ProtocolCommentaryRepository protocolCommentaryRepository;

  public ProtocolDataService(ProtocolRepository protocolRepository,
      ProtocolStepRepository protocolStepRepository,
      ProtocolCommentaryRepository protocolCommentaryRepository) {
    this.protocolRepository = protocolRepository;
    this.protocolStepRepository = protocolStepRepository;
    this.protocolCommentaryRepository = protocolCommentaryRepository;
  }

  public List<Protocol> getAllProtocols() {
    return this.protocolRepository.findAll();
  }

  public Optional<Protocol> getProtocolById(Long id) {
    return this.protocolRepository.findById(id);
  }

  public Optional<Protocol> getByProtocolTemplateIdAndHouseholdId(Long protocolTemplateId,
      Long householdId) {
    return this.protocolRepository.findByAssignedHouseholdId(householdId).stream()
        .filter(protocol -> protocol.getProtocolTemplate()
            .getId()
            .equals(protocolTemplateId))
        .findFirst();
  }

  public List<Protocol> getByProtocolTemplateId(Long protocolTemplateId) {
    return this.protocolRepository.findByProtocolTemplate_Id(protocolTemplateId);
  }

  public List<Protocol> getByHouseholdId(Long householdId) {
    return this.protocolRepository.findByAssignedHouseholdId(householdId);
  }

  @Transactional
  public Protocol saveProtocol(Protocol protocolToBeCreated) {
    //Save the Protocol
    Set<ProtocolStep> protocolSteps = protocolToBeCreated.getProtocolSteps();
    protocolToBeCreated.setProtocolSteps(null);
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
    return this.protocolRepository.saveAndFlush(protocolToBeUpdated);
  }

  public List<Protocol> getAllScheduledProtocols() {
    return this.protocolRepository.findByRecurrenceType(RecurrenceTypeEnum.ON_SCHEDULE);
  }
}
