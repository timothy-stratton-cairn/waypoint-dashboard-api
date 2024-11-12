package com.cairn.waypoint.dashboard.service.helper;

import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolStep;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplateLinkedStepTemplate;
import com.cairn.waypoint.dashboard.entity.StepCategory;
import com.cairn.waypoint.dashboard.entity.StepTemplate;
import com.cairn.waypoint.dashboard.entity.enumeration.StepStatusEnum;
import com.cairn.waypoint.dashboard.mapper.ProtocolMapper;
import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
import com.cairn.waypoint.dashboard.service.data.StepTemplateDataService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Transactional
public class ProtocolTemplateHelperService {

  private final ProtocolDataService protocolDataService;
  private final StepTemplateDataService stepTemplateDataService;

  public ProtocolTemplateHelperService(ProtocolDataService protocolDataService,
      StepTemplateDataService stepTemplateDataService) {
    this.protocolDataService = protocolDataService;
    this.stepTemplateDataService = stepTemplateDataService;
  }

  @Transactional
  public void removeProtocolStepsNotAssociatedWithTheUpdatedProtocolTemplate(
      ProtocolTemplate updatedProtocolTemplate) {
    List<Protocol> associatedProtocols = this.protocolDataService.getByProtocolTemplateId(
        updatedProtocolTemplate.getId());

    List<Long> updatedStepTemplateIdList = updatedProtocolTemplate.getProtocolTemplateSteps()
        .stream()
        .map(ProtocolTemplateLinkedStepTemplate::getStepTemplate)
        .map(StepTemplate::getId)
        .toList();

    BiFunction<Set<ProtocolStep>, List<Long>, Set<ProtocolStep>> removeProtocolStepBiFunction =
        (protocolSteps, updatedStepTemplateIds) -> {
          protocolSteps.stream()
              .filter(protocolStep -> !updatedStepTemplateIds.contains(
                  protocolStep.getTemplate().getId()))
              .forEach(protocolStep -> {
                protocolStep.setActive(Boolean.FALSE);
              });
          return protocolSteps;
        };

    associatedProtocols.stream()
        .peek(protocol -> protocol.setProtocolSteps(
            removeProtocolStepBiFunction.apply(protocol.getProtocolSteps(),
                updatedStepTemplateIdList)))
        .forEach(this.protocolDataService::updateProtocol);
  }

  @Transactional
  public void addProtocolStepsNotAssociatedWithProtocolsMadeFromTheUpdatedProtocolTemplate(
      ProtocolTemplate updatedProtocolTemplate, String modifiedBy) {
    List<Protocol> associatedProtocols = this.protocolDataService.getByProtocolTemplateId(
        updatedProtocolTemplate.getId());

    List<Long> updatedStepTemplateIdList = updatedProtocolTemplate.getProtocolTemplateSteps()
        .stream()
        .map(ProtocolTemplateLinkedStepTemplate::getStepTemplate)
        .map(StepTemplate::getId)
        .collect(Collectors.toList());

    associatedProtocols.stream()
        .peek(protocol -> protocol.setProtocolSteps(
            populatedNewProtocolSteps(protocol, protocol.getProtocolSteps(),
                updatedStepTemplateIdList, modifiedBy)))
        .forEach(this.protocolDataService::updateProtocol);
  }

  private Set<ProtocolStep> populatedNewProtocolSteps(Protocol parentProtocol,
      Set<ProtocolStep> protocolSteps, List<Long> updatedStepTemplateIds, String modifiedBy) {
    AtomicInteger ordinalIndexStart = new AtomicInteger(protocolSteps.size());

    updatedStepTemplateIds.removeIf(updatedStepTemplateId -> protocolSteps.stream().anyMatch(
        protocolStep -> protocolStep.getTemplate().getId().equals(updatedStepTemplateId)));

    Set<ProtocolStep> newProtocolSteps = updatedStepTemplateIds.stream()
        .map(this.stepTemplateDataService::getStepTemplateById)
        .flatMap(Optional::stream)
        .map(stepTemplate -> {
          ProtocolStep protocolStep = ProtocolMapper.INSTANCE.protocolStepTemplateToProtocolStep(
              stepTemplate);
          protocolStep.setModifiedBy(modifiedBy);
          protocolStep.setTemplate(stepTemplate);
          protocolStep.setOrdinalIndex(ordinalIndexStart.getAndIncrement());
          protocolStep.setCategory(StepCategory.builder()
              .modifiedBy(modifiedBy)
              .stepTemplateCategory(stepTemplate.getCategory())
              .build());
          protocolStep.setStatus(StepStatusEnum.TODO);
          return protocolStep;
        })
        .collect(Collectors.toSet());

    protocolSteps.addAll(newProtocolSteps);

    return protocolSteps;
  }
}

