package com.cairn.waypoint.dashboard.utility.protocoltriggering;

import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.enumeration.ProtocolStatusEnum;
import com.cairn.waypoint.dashboard.entity.enumeration.RecurrenceTypeEnum;
import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolStepDataService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Component
public class OnScheduleObserver implements StatusObserver {

  private final RecurrenceTypeEnum subscribedRecurrenceType = RecurrenceTypeEnum.ON_SCHEDULE;

  private final List<ProtocolStatusEnum> relevantStatuses = Arrays.asList(
      ProtocolStatusEnum.IN_PROGRESS,
      ProtocolStatusEnum.COMPLETED
  );

  private final EntityManager entityManager;
  private final ProtocolDataService protocolDataService;
  private final ProtocolStepDataService protocolStepDataService;

  public OnScheduleObserver(EntityManager entityManager,
      ProtocolDataService protocolDataService,
      ProtocolStepDataService protocolStepDataService
  ) {
    this.entityManager = entityManager;
    this.protocolDataService = protocolDataService;
    this.protocolStepDataService = protocolStepDataService;
  }

  @Override
  @Transactional
  public void update(Protocol protocol) {
    if (relevantStatuses.contains(protocol.getStatus()) &&
        protocolIsReadyForNextScheduledOccurrence(protocol)) {
      this.createReoccurrence(protocol);
    }
  }

  @Override
  public void createReoccurrence(Protocol protocolToReoccur) {
    log.info("Creating new scheduled occurrence of Protocol with ID [{}]",
        protocolToReoccur.getId());
    createProtocolRecurrence(protocolToReoccur, protocolDataService,
        protocolStepDataService, entityManager);
    log.info("Protocol with ID [{}] new instance created successfully", protocolToReoccur.getId());
  }

  private boolean protocolIsReadyForNextScheduledOccurrence(Protocol protocol) {
    return !protocol.getCreated()
        .plusYears(protocol.getReoccurInYears())
        .plusMonths(protocol.getReoccurInMonths())
        .plusDays(protocol.getReoccurInDays())
        .toLocalDate()
        .isAfter(LocalDate.now());
  }

}
