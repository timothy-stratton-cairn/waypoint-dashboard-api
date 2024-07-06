package com.cairn.waypoint.dashboard.utility.protocoltriggering;

import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProtocolTriggerScheduler {

  private final ProtocolDataService protocolDataService;

  public ProtocolTriggerScheduler(ProtocolDataService protocolDataService) {
    this.protocolDataService = protocolDataService;
  }

  public void runSchedulerForProtocols() {
    protocolDataService.getAllScheduledProtocols().forEach(StatusManager::notify);
  }

  @Transactional
  @Scheduled(cron = "${waypoint.dashboard.protocol-triggering.schedule}")
  public void kickOffProtocolTriggerSchedule() {
    log.info("Running Protocol Trigger Scheduler...");
    runSchedulerForProtocols();
    log.info("Protocol Trigger Scheduler Completed.");
  }
}
