package com.cairn.waypoint.dashboard.endpoints.ops;

import com.cairn.waypoint.dashboard.utility.protocoltriggering.ProtocolTriggerScheduler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ScheduledProtocolManualTriggeringEndpoint {

  public static final String PATH = "/api/ops/trigger-scheduled-protocols";
  private final ProtocolTriggerScheduler protocolTriggerScheduler;

  public ScheduledProtocolManualTriggeringEndpoint(
      ProtocolTriggerScheduler protocolTriggerScheduler) {
    this.protocolTriggerScheduler = protocolTriggerScheduler;
  }

  @PostMapping(PATH)
  @PreAuthorize("hasAuthority('SCOPE_admin.full')")
  @Operation(
      summary = "Allows a user to trigger recurrences of all scheduled protocols",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"))
  public ResponseEntity<?> triggerScheduledProtocols() {
    log.info("Running the scheduled protocol trigger job manually...");
    protocolTriggerScheduler.runSchedulerForProtocols();
    return ResponseEntity.ok("Scheduled Protocols have been successfully triggered.");
  }
}
