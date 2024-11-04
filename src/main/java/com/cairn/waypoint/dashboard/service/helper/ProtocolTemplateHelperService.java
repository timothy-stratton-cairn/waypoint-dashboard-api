package com.cairn.waypoint.dashboard.service.helper;

//import com.cairn.waypoint.dashboard.entity.Homework;
import com.cairn.waypoint.dashboard.entity.HomeworkResponse;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolStep;

import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplateLinkedStepTemplate;
import com.cairn.waypoint.dashboard.entity.StepCategory;
import com.cairn.waypoint.dashboard.entity.StepTemplate;
import com.cairn.waypoint.dashboard.entity.StepTemplateLinkedHomeworkTemplate;
import com.cairn.waypoint.dashboard.entity.enumeration.StepStatusEnum;
import com.cairn.waypoint.dashboard.mapper.ProtocolMapper;
import com.cairn.waypoint.dashboard.service.data.HomeworkDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolStepLinkedHomeworkService;
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

  public ProtocolTemplateHelperService(ProtocolDataService protocolDataService,StepTemplateDataService stepTemplateDataService) {
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
                /*if (protocolStep.getLinkedHomework() != null) {
                  protocolStep.getLinkedHomework().forEach(
                      protocolStepLinkedHomework -> protocolStepLinkedHomework.setActive(
                          Boolean.FALSE));
                }*/
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
          //protocolStep.setParentProtocol(parentProtocol);

          return protocolStep;
        })
        .collect(Collectors.toSet());

    //createClientHomework(newProtocolSteps, parentProtocol.getAssignedHouseholdId(), modifiedBy);

    protocolSteps.addAll(newProtocolSteps);

    return protocolSteps;
  }
  //TODO this will need to be reworked to work without Homework 
  
  /*public void generateAndSaveClientHomework(Protocol createdProtocol, String modifiedBy) {
    // Save independent pieces first
    List<Homework> homeworkList = createClientHomework(createdProtocol.getProtocolSteps(),
        createdProtocol.getAssignedHouseholdId(),
        modifiedBy);

    for (Homework homework : homeworkList) {
      Set<HomeworkResponse> homeworkResponses = homework.getHomeworkQuestions();
      homework.setHomeworkQuestions(null);
      //Homework savedHomework = this.homeworkDataService.saveHomework(homework);
      homework.setHomeworkQuestions(homeworkResponses);

      savedHomework.getHomeworkQuestions()
          .forEach(homeworkResponse -> homeworkResponse.setHomework(savedHomework));

      this.homeworkDataService.saveHomework(homework);
    }*/

    /*List<ProtocolStepLinkedHomework> homeworkToSave = createdProtocol.getProtocolSteps().stream()
        .map(ProtocolStep::getLinkedHomework)
        .flatMap(Set::stream)
        .collect(Collectors.toList());

    for (ProtocolStepLinkedHomework homework : homeworkToSave) {
      protocolStepLinkedHomeworkService.saveProtocolStepLinkedHomework(homework);
    }
  }*/
  
 //TODO This will need to be reworked to work without Homework
  /*private List<Homework> createClientHomework(Collection<ProtocolStep> protocolSteps,
      Long householdId,
      String modifiedBy) {
    return protocolSteps.stream()
        .map(protocolStep -> {
          if (protocolStep.getTemplate().getStepTemplateLinkedHomeworks() != null) {
            List<Homework> protocolStepHomeworks = protocolStep.getTemplate()
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
                    .assignedHouseholdId(householdId)
                    .build())
                .collect(Collectors.toList());

            protocolStepHomeworks.stream()
                .map(homework -> ProtocolStepLinkedHomework.builder()
                    .modifiedBy(modifiedBy)
                    .homework(homework)
                    .step(protocolStep)
                    .build())
                .forEach(protocolStep::addLinkedHomework);

            return protocolStepHomeworks;
          } else {
            return null;
          }
        })
        .filter(Objects::nonNull)
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }*/
}

