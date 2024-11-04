package com.cairn.waypoint.dashboard.utility.protocoltriggering;

import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolStep;

import com.cairn.waypoint.dashboard.entity.ProtocolStepNote;
import com.cairn.waypoint.dashboard.entity.enumeration.ProtocolCommentTypeEnum;
import com.cairn.waypoint.dashboard.entity.enumeration.ProtocolStatusEnum;
import com.cairn.waypoint.dashboard.entity.enumeration.RecurrenceTypeEnum;
import com.cairn.waypoint.dashboard.entity.enumeration.StepStatusEnum;
import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolStepDataService;
//import com.cairn.waypoint.dashboard.service.data.ProtocolStepLinkedHomeworkService;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.util.SerializationUtils;

public interface StatusObserver {

  void update(Protocol protocol);

  void createReoccurrence(Protocol protocol);

  RecurrenceTypeEnum getSubscribedRecurrenceType();

  default void createProtocolRecurrence(Protocol protocol,
      ProtocolDataService protocolDataService,
      ProtocolStepDataService protocolStepDataService,
      EntityManager entityManager) {
    protocol.setStatus(ProtocolStatusEnum.COMPLETED_WITH_TRIGGERED_RECURRENCE);
    protocol.setModifiedBy("triggering-system");
    protocol.setLastStatusUpdateTimestamp(LocalDateTime.now());
    protocol.setCompletionDate(LocalDate.now());
    protocol.getProtocolSteps().forEach(protocolStep -> {
      protocolStep.setModifiedBy("triggering-system");
      protocolStep.setStatus(StepStatusEnum.CONDITIONAL_COMPLETION);
      protocolStep.getNotes().add(ProtocolStepNote.builder()
          .modifiedBy("triggering-system")
          .protocolStep(protocolStep)
          .note("New Occurrence Triggered. Completing Stale Steps.")
          .originalCommenter("triggering-system")
          .noteType(ProtocolCommentTypeEnum.CONDITIONAL_COMPLETION_NOTE)
          .build());
    });

    protocolStepDataService.saveProtocolStepList(new ArrayList<>(protocol.getProtocolSteps()));

    Protocol protocolToReoccur = protocolDataService.updateProtocol(protocol);

    entityManager.detach(protocolToReoccur); //detach the entity to create a clone of it
    protocolToReoccur = SerializationUtils.clone(protocolToReoccur);

    protocolToReoccur.setId(null);//remove the existing ID from the detached entity
    protocolToReoccur.setCreated(
        LocalDateTime.now());//IMPORTANT -- will break out of the systemic recursion
    protocolToReoccur.setModifiedBy("triggering-system");
    protocolToReoccur.setStatus(ProtocolStatusEnum.IN_PROGRESS);
    protocolToReoccur.setLastStatusUpdateTimestamp(LocalDateTime.now());
    protocolToReoccur.setCompletionDate(null);
    protocolToReoccur.setDueDate(LocalDate.now()
        .plusYears(protocol.getProtocolTemplate().getDefaultDueByInYears())
        .plusMonths(protocol.getProtocolTemplate().getDefaultDueByInMonths())
        .plusDays(protocol.getProtocolTemplate().getDefaultDueByInDays()));

    protocolToReoccur.getProtocolSteps().forEach(protocolStep -> {
      entityManager.detach(protocolStep);
      protocolStep.setCategory(protocol.getProtocolSteps().stream()
          .filter(referenceProtocolStep ->
              referenceProtocolStep.getId().equals(protocolStep.getId()))
          .findFirst()
          .orElseThrow()
          .getCategory());

      protocolStep.setId(null);
      protocolStep.setModifiedBy("triggering-system");
      protocolStep.setStatus(StepStatusEnum.TODO);
      protocolStep.getNotes().removeIf(protocolStepNote -> protocolStepNote.getNoteType().equals(
          ProtocolCommentTypeEnum.CONDITIONAL_COMPLETION_NOTE));
    });

    protocolToReoccur.getComments().forEach(protocolCommentary -> {
      entityManager.detach(protocolCommentary);
      protocolCommentary.setCommentType(protocol.getComments().stream()
          .filter(referenceProtocolComment ->
              referenceProtocolComment.getId().equals(protocolCommentary.getId()))
          .findFirst()
          .orElseThrow()
          .getCommentType());
      protocolCommentary.setId(null);
      protocolCommentary.setModifiedBy("triggering-system");
    });

    /*Set<ProtocolStepLinkedHomework> stepLinkedHomeworkSet = protocolToReoccur.getProtocolSteps()
        .stream()
        .map(ProtocolStep::getLinkedHomework)
        .flatMap(Set::stream)
        .collect(Collectors.toSet()); */
    protocolToReoccur.getProtocolSteps();
        //.forEach(protocolStep -> protocolStep.setLinkedHomework(null));

    //Protocol protocolReoccurrence = protocolDataService.saveProtocol(protocolToReoccur);

   /* stepLinkedHomeworkSet.stream()
        .map(stepLinkedHomework ->
            ProtocolStepLinkedHomework.builder()
                .modifiedBy("triggering-system")
                .step(protocolReoccurrence.getProtocolSteps().stream()
                    .filter(protocolStep ->
                        protocolStep.getTemplate().getId()
                            .equals(stepLinkedHomework.getStep().getTemplate().getId()))
                    .findFirst()
                    .orElseThrow())
                .homework(protocol.getProtocolSteps().stream()
                    .map(ProtocolStep::getLinkedHomework)
                    .flatMap(Set::stream)
                    .map(ProtocolStepLinkedHomework::getHomework)
                    .filter(homework -> homework.getName()
                        .equals(stepLinkedHomework.getHomework().getName()))
                    .findFirst()
                    .orElseThrow())
                .build())
        .forEach(protocolStepLinkedHomeworkService::saveProtocolStepLinkedHomework);*/
  }
}
