package com.cairn.waypoint.dashboard.endpoints.protocol;

import com.cairn.waypoint.dashboard.endpoints.protocol.dto.AssociatedStepsListDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolCommentDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolCommentListDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolListDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolStepDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolStepNoteDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolStepNoteListDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.service.ProtocolCalculationService;
import com.cairn.waypoint.dashboard.service.ProtocolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Protocol")
public class GetAllProtocolsEndpoint {

  public static final String PATH = "/api/protocol";

  private final ProtocolService protocolService;

  public GetAllProtocolsEndpoint(ProtocolService protocolService) {
    this.protocolService = protocolService;
  }

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieves all protocols.",
      description = "Retrieves all protocols. Requires the `protocol.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ProtocolListDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(schema = @Schema(hidden = true))})})
  public ResponseEntity<ProtocolListDto> getAllProtocols(Principal principal) {
    log.info("User [{}] is Retrieving All Protocols", principal.getName());
    return ResponseEntity.ok(
        ProtocolListDto.builder()
            .protocols(
                this.protocolService.getAllProtocols().stream()
                    .map(protocol -> ProtocolDto.builder()
                        .id(protocol.getId())
                        .name(protocol.getName())
                        .description(protocol.getDescription())
                        .goal(protocol.getGoal())
                        .goalProgress(protocol.getGoalProgress())
                        .protocolComments(ProtocolCommentListDto.builder()
                            .comments(protocol.getComments().stream()
                                .map(protocolComment -> ProtocolCommentDto.builder()
                                    .takenAt(protocolComment.getCreated())
                                    .takenBy(protocolComment.getOriginalCommenter())
                                    .comment(protocolComment.getComment())
                                    .build())
                                .toList())
                            .build())
                        .needsAttention(protocol.getMarkedForAttention())
                        .lastStatusUpdateTimestamp(protocol.getLastStatusUpdateTimestamp())
                        .completionPercentage(
                            ProtocolCalculationService.getProtocolCompletionPercentage(protocol))
                        .associatedSteps(AssociatedStepsListDto.builder()
                            .steps(protocol.getProtocolSteps().stream()
                                .map(protocolStep -> ProtocolStepDto.builder()
                                    .id(protocolStep.getId())
                                    .name(protocolStep.getName())
                                    .description(protocolStep.getDescription())
                                    .stepNotes(ProtocolStepNoteListDto.builder()
                                        .notes(protocolStep.getNotes().stream()
                                            .map(protocolStepNote -> ProtocolStepNoteDto.builder()
                                                .takenAt(protocolStepNote.getCreated())
                                                .takenBy(protocolStepNote.getOriginalCommenter())
                                                .note(protocolStepNote.getNote())
                                                .build())
                                            .toList())
                                        .build())
                                    .status(protocolStep.getStatus().getInstance().getName())
                                    .category(
                                        protocolStep.getCategory().getTemplateCategory().getName())
                                    .build())
                                .collect(Collectors.toList()))
                            .build())
                        .build())
                    .toList())
            .build()
    );
  }

}
