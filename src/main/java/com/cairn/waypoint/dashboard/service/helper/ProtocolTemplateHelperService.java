package com.cairn.waypoint.dashboard.service.helper;

import com.cairn.waypoint.dashboard.endpoints.protocol.mapper.ProtocolMapper;
import com.cairn.waypoint.dashboard.entity.Homework;
import com.cairn.waypoint.dashboard.entity.HomeworkResponse;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolStep;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplateLinkedStepTemplate;
import com.cairn.waypoint.dashboard.entity.StepCategory;
import com.cairn.waypoint.dashboard.entity.StepTemplate;
import com.cairn.waypoint.dashboard.entity.StepTemplateLinkedHomeworkTemplate;
import com.cairn.waypoint.dashboard.entity.enumeration.StepStatusEnum;
import com.cairn.waypoint.dashboard.service.data.HomeworkDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
import com.cairn.waypoint.dashboard.service.data.StepTemplateDataService;
import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Transactional
public class ProtocolTemplateHelperService {

  private final ProtocolDataService protocolDataService;
  private final StepTemplateDataService stepTemplateDataService;
  private final HomeworkDataService homeworkDataService;

  public ProtocolTemplateHelperService(ProtocolDataService protocolDataService,
      StepTemplateDataService stepTemplateDataService, HomeworkDataService homeworkDataService) {
    this.protocolDataService = protocolDataService;
    this.stepTemplateDataService = stepTemplateDataService;
    this.homeworkDataService = homeworkDataService;
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

    BiFunction<Set<ProtocolStep>, List<Long>, Set<ProtocolStep>> removeProtocolStepBiConsumer =
        (protocolSteps, updatedStepTemplateIds) -> {
          protocolSteps.stream()
              .filter(protocolStep -> !updatedStepTemplateIds.contains(
                  protocolStep.getTemplate().getId()))
              .forEach(protocolStep -> {
                protocolStep.setActive(Boolean.FALSE);
                if (protocolStep.getLinkedHomework() != null) {
                  protocolStep.getLinkedHomework().setActive(Boolean.FALSE);
                }
              });
          return protocolSteps;
        };

    associatedProtocols.stream()
        .peek(protocol -> protocol.setProtocolSteps(
            removeProtocolStepBiConsumer.apply(protocol.getProtocolSteps(),
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
              .templateCategory(stepTemplate.getCategory())
              .build());
          protocolStep.setStatus(StepStatusEnum.TODO);
          protocolStep.setParentProtocol(parentProtocol);

          return protocolStep;
        })
        .collect(Collectors.toSet());

    createClientHomework(newProtocolSteps, parentProtocol.getAssignedHouseholdId(), modifiedBy);

    protocolSteps.addAll(newProtocolSteps);

    return protocolSteps;
  }

  public void generateAndSaveClientHomework(Protocol createdProtocol, String modifiedBy) {
    List<Homework> homeworkList = createClientHomework(createdProtocol.getProtocolSteps(),
        createdProtocol.getAssignedHouseholdId(),
        modifiedBy);
    homeworkList.forEach(homework -> {
      Set<HomeworkResponse> homeworkResponses = homework.getHomeworkQuestions();
      homework.setHomeworkQuestions(null);
      Homework savedHomework = this.homeworkDataService.saveHomework(homework);
      homework.setHomeworkQuestions(homeworkResponses);

      savedHomework.getHomeworkQuestions()
          .forEach(homeworkResponse -> homeworkResponse.setHomework(savedHomework));

      this.homeworkDataService.saveHomework(homework);
    });
  }

  private List<Homework> createClientHomework(Collection<ProtocolStep> protocolSteps,
      Long householdId,
      String modifiedBy) {
    return protocolSteps.stream()
        .map(protocolStep -> {
          if (protocolStep.getTemplate().getStepTemplateLinkedHomeworks() != null) {
            return protocolStep.getTemplate()
                .getStepTemplateLinkedHomeworks().stream()
                .map(StepTemplateLinkedHomeworkTemplate::getHomeworkTemplate)
                .map(homeworkTemplate -> Homework.builder()
                    .modifiedBy(modifiedBy)
                    .name(homeworkTemplate.getName())
                    .description(homeworkTemplate.getDescription())
                    .homeworkTemplate(homeworkTemplate)
                    .homeworkQuestions(homeworkTemplate.getHomeworkQuestions().stream()
                        .map(question -> HomeworkResponse.builder()
                            .modifiedBy(modifiedBy)
                            .homeworkQuestion(question.getHomeworkQuestion())
                            .ordinalIndex(question.getOrdinalIndex())
                            .build())
                        .collect(Collectors.toSet()))
                    .associatedProtocolStep(protocolStep)
                    .assignedHouseholdId(householdId)
                    .build())
                .collect(Collectors.toList());
          } else {
            return null;
          }
        })
        .filter(Objects::nonNull)
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }
}
