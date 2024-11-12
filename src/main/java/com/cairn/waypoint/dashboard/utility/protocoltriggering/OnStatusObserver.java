package com.cairn.waypoint.dashboard.utility.protocoltriggering;

import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.enumeration.RecurrenceTypeEnum;
import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolStepDataService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Component
public class OnStatusObserver implements StatusObserver {

  private final RecurrenceTypeEnum subscribedRecurrenceType = RecurrenceTypeEnum.ON_STATUS;

  private final EntityManager entityManager;
  private final ProtocolDataService protocolDataService;
  private final ProtocolStepDataService protocolStepDataService;
  //private final ProtocolStepLinkedHomeworkService protocolStepLinkedHomeworkService;

  public OnStatusObserver(EntityManager entityManager,
      ProtocolDataService protocolDataService,
      ProtocolStepDataService protocolStepDataService
      //ProtocolStepLinkedHomeworkService protocolStepLinkedHomeworkService
  ) {
    this.entityManager = entityManager;
    this.protocolDataService = protocolDataService;
    this.protocolStepDataService = protocolStepDataService;
    //this.protocolStepLinkedHomeworkService = protocolStepLinkedHomeworkService;
  }

  @Override
  @Transactional
  public void update(Protocol protocol) {
    if (protocol.getStatus().equals(protocol.getTriggeringStatus())) {
      this.createReoccurrence(protocol);
    }
  }

  @Override
  public void createReoccurrence(Protocol protocolToReoccur) {
    log.info("Creating new occurrence of Protocol with ID [{}] based on status",
        protocolToReoccur.getId());
    createProtocolRecurrence(protocolToReoccur, protocolDataService,
        //protocolStepLinkedHomeworkService,
        protocolStepDataService, entityManager);
    log.info("Protocol with ID [{}] new instance created successfully", protocolToReoccur.getId());
  }
}
