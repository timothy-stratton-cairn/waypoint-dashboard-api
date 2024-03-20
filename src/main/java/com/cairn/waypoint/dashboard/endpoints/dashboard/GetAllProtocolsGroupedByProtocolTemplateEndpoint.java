package com.cairn.waypoint.dashboard.endpoints.dashboard;

import com.cairn.waypoint.dashboard.endpoints.dashboard.dto.GlobalProtocolViewDto;
import com.cairn.waypoint.dashboard.endpoints.dashboard.dto.GlobalProtocolViewListDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolListDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.service.ProtocolCalculationService;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.enumeration.StepStatusEnum;
import com.cairn.waypoint.dashboard.service.ProtocolService;
import com.cairn.waypoint.dashboard.service.ProtocolTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Dashboard")
public class GetAllProtocolsGroupedByProtocolTemplateEndpoint {

  public static final String PATH = "/api/dashboard/protocol";

  private final ProtocolTemplateService protocolTemplateService;
  private final ProtocolService protocolService;

  public GetAllProtocolsGroupedByProtocolTemplateEndpoint(
      ProtocolTemplateService protocolTemplateService, ProtocolService protocolService) {
    this.protocolTemplateService = protocolTemplateService;
    this.protocolService = protocolService;
  }

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieves all protocols grouped by Protocol Template.",
      description = "Retrieves all protocols grouped by Protocol Template. Requires the `protocol.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ProtocolListDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(schema = @Schema(hidden = true))})})
  public ResponseEntity<GlobalProtocolViewListDto> getAllProtocols(Principal principal) {
    log.info("User [{}] is Retrieving All Protocols for the Dashboard View", principal.getName());

    List<ProtocolTemplate> protocolTemplates = this.protocolTemplateService.getAllProtocolTemplates();

    List<GlobalProtocolViewDto> globalProtocolViewDtos = new ArrayList<>();

    protocolTemplates
        .forEach(protocolTemplate -> {
          List<Protocol> assignedProtocols = this.protocolService.getByProtocolTemplateId(
              protocolTemplate.getId());

          if (!assignedProtocols.isEmpty()) {
            globalProtocolViewDtos.add(GlobalProtocolViewDto.builder()
                .protocolTemplateId(protocolTemplate.getId())
                .protocolTemplateName(protocolTemplate.getName())
                .numAssignedUsers(assignedProtocols.size())
                .numStepsTodo(assignedProtocols.stream().map(
                    protocol -> protocol.getProtocolSteps().stream()
                        .filter(protocolStep -> protocolStep.getStatus().equals(
                            StepStatusEnum.TODO)).count()).reduce(0L, Long::sum))
                .numStepsInProgress(assignedProtocols.stream().map(
                    protocol -> protocol.getProtocolSteps().stream()
                        .filter(protocolStep -> protocolStep.getStatus().equals(
                            StepStatusEnum.IN_PROGRESS)).count()).reduce(0L, Long::sum))
                .numStepsDone(assignedProtocols.stream().map(
                    protocol -> protocol.getProtocolSteps().stream()
                        .filter(protocolStep -> protocolStep.getStatus().equals(
                            StepStatusEnum.DONE)).count()).reduce(0L, Long::sum))
                .completionPercentage(assignedProtocols.stream()
                    .map(ProtocolCalculationService::getProtocolCompletionPercentage).reduce(
                        BigDecimal.ZERO, BigDecimal::add)
                    .divide(new BigDecimal(assignedProtocols.size()), RoundingMode.HALF_UP))
                .build());
          } else {
            globalProtocolViewDtos.add(GlobalProtocolViewDto.builder()
                .protocolTemplateId(protocolTemplate.getId())
                .protocolTemplateName(protocolTemplate.getName())
                .build());
          }
        });

    return ResponseEntity.ok(
        GlobalProtocolViewListDto.builder()
            .protocols(globalProtocolViewDtos)
            .build()
    );
  }

}
