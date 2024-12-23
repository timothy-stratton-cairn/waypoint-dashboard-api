package com.cairn.waypoint.dashboard.endpoints.protocol;

import com.cairn.waypoint.dashboard.endpoints.filedownload.DownloadStepAttachmentEndpoint;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.AssociatedStepsListDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolCommentDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolCommentListDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolListDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolStepAttachmentDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolStepAttachmentListDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolStepDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolStepNoteDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolStepNoteListDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.UpcomingProtocolDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.UpcomingProtocolListDto;
import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
import com.cairn.waypoint.dashboard.service.helper.ProtocolCalculationHelperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.time.LocalDate;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Protocol")
public class GetAllUpcomingProtocolsEndpoint {

  public static final String PATH = "/api/protocol/upcoming";
  private final ProtocolDataService protocolDataService;
  @Value("${waypoint.dashboard.base-url}")
  private String baseUrl;

  public GetAllUpcomingProtocolsEndpoint(ProtocolDataService protocolDataService) {
    this.protocolDataService = protocolDataService;
  }

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieves all upcoming protocols.",
      description = "Retrieves all upcoming protocols. Requires the `protocol.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ProtocolListDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(schema = @Schema(hidden = true))})})
  public ResponseEntity<UpcomingProtocolListDto> getAllUpcomingProtocols(Principal principal,
      @RequestParam("limitingDate") LocalDate limitingDate) {
    log.info("User [{}] is Retrieving Upcoming All Protocols to Date [{}]", principal.getName(),
        limitingDate);
    return ResponseEntity.ok(
        UpcomingProtocolListDto.builder()
            .protocols(
                this.protocolDataService.getAllUpcomingProtocols(limitingDate).stream()
                    .map(protocol -> UpcomingProtocolDto.builder()
                        .id(protocol.getId())
                        .name(protocol.getName())
                        .description(protocol.getDescription())
                        .goal(protocol.getGoal())
                        .goalProgress(protocol.getGoalProgress())
                        .createdAt(protocol.getCreated())
                        .dueBy(protocol.getDueDate())
                        .completedOn(protocol.getCompletionDate())
                        .willReoccurOn(protocol.getCreated()
                            .plusDays(protocol.getReoccurInDays())
                            .plusMonths(protocol.getReoccurInMonths())
                            .plusYears(protocol.getReoccurInYears())
                            .toLocalDate())
                        .protocolComments(ProtocolCommentListDto.builder()
                            .comments(protocol.getComments().stream()
                                .map(protocolComment -> ProtocolCommentDto.builder()
                                    .commentId(protocolComment.getId())
                                    .takenAt(protocolComment.getCreated())
                                    .takenBy(protocolComment.getOriginalCommenter())
                                    .comment(protocolComment.getComment())
                                    .commentType(protocolComment.getCommentType().name())
                                    .build())
                                .toList())
                            .build())
                        .needsAttention(protocol.getMarkedForAttention())
                        .lastStatusUpdateTimestamp(protocol.getLastStatusUpdateTimestamp())
                        .status(protocol.getStatus().name())
                        .completionPercentage(
                            ProtocolCalculationHelperService.getProtocolCompletionPercentage(
                                protocol))
                        .associatedSteps(AssociatedStepsListDto.builder()
                            .steps(protocol.getProtocolSteps().stream()
                                .map(protocolStep -> ProtocolStepDto.builder()
                                    .id(protocolStep.getId())
                                    .name(protocolStep.getName())
                                    .description(protocolStep.getDescription())
                                    .stepNotes(ProtocolStepNoteListDto.builder()
                                        .notes(protocolStep.getNotes().stream()
                                            .map(protocolStepNote -> ProtocolStepNoteDto.builder()
                                                .noteId(protocolStepNote.getId())
                                                .takenAt(protocolStepNote.getCreated())
                                                .takenBy(protocolStepNote.getOriginalCommenter())
                                                .note(protocolStepNote.getNote())
                                                .build())
                                            .toList())
                                        .build())
                                    .stepAttachments(ProtocolStepAttachmentListDto.builder()
                                        .attachments(protocolStep.getAttachments().stream()
                                            .map(
                                                protocolStepAttachment -> ProtocolStepAttachmentDto.builder()
                                                    .filename(protocolStepAttachment.getFilename())
                                                    .downloadUrl(baseUrl
                                                        + DownloadStepAttachmentEndpoint.PATH.replace(
                                                        "{fileGuid}",
                                                        protocolStepAttachment.getFileGuid()))
                                                    .build())
                                            .toList())
                                        .build())
                                    .status(protocolStep.getStatus().getInstance().getName())
                                    .category(
                                        protocolStep.getCategory().getStepTemplateCategory()
                                            .getName())
                                    .stepTemplateId(protocolStep.getTemplate().getId())
                                    .build())
                                .collect(Collectors.toList()))
                            .build())
                        .build())
                    .toList())
            .build()
    );
  }

}
