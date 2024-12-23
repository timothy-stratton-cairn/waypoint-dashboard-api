package com.cairn.waypoint.dashboard.endpoints.protocol;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.filedownload.DownloadStepAttachmentEndpoint;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.AssociatedStepsListDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolCommentDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolCommentListDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolStepAttachmentDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolStepAttachmentListDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolStepDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolStepNoteDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolStepNoteListDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.RecurrenceDetailsDto;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
import com.cairn.waypoint.dashboard.service.helper.ProtocolCalculationHelperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Protocol")
public class GetProtocolByIdEndpoint {

  public static final String PATH = "/api/protocol/{protocolId}";
  private final ProtocolDataService protocolDataService;
  @Value("${waypoint.dashboard.base-url}")
  private String baseUrl;

  public GetProtocolByIdEndpoint(ProtocolDataService protocolDataService) {
    this.protocolDataService = protocolDataService;
  }

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieves a protocol by it's ID.",
      description = "Retrieves a protocol by it's ID. Requires the `protocol.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ProtocolDetailsDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> getProtocolById(@PathVariable Long protocolId, Principal principal) {
    log.info("User [{}] is Retrieving Protocol with ID [{}]", principal.getName(), protocolId);

    final ResponseEntity<?>[] response = new ResponseEntity<?>[1];
    this.protocolDataService.getProtocolById(protocolId)
        .ifPresentOrElse(
            returnedProtocol -> response[0] = generateSuccessResponse(returnedProtocol),
            () -> response[0] = generateFailureResponse(protocolId)
        );

    return response[0];
  }

  public ResponseEntity<ProtocolDetailsDto> generateSuccessResponse(Protocol returnedProtocol) {
    return ResponseEntity.ok(
        ProtocolDetailsDto.builder()
            .id(returnedProtocol.getId())
            .name(returnedProtocol.getName())
            .description(returnedProtocol.getDescription())
            .goal(returnedProtocol.getGoal())
            .userId(returnedProtocol.getUserId())
            .goalProgress(returnedProtocol.getGoalProgress())
            .createdAt(returnedProtocol.getCreated())
            .dueBy(returnedProtocol.getDueDate())
            .completedOn(returnedProtocol.getCompletionDate())
            .protocolComments(ProtocolCommentListDto.builder()
                .comments(returnedProtocol.getComments().stream()
                    .map(protocolComment -> ProtocolCommentDto.builder()
                        .commentId(protocolComment.getId())
                        .takenAt(protocolComment.getCreated())
                        .takenBy(protocolComment.getOriginalCommenter())
                        .comment(protocolComment.getComment())
                        .commentType(protocolComment.getCommentType().name())
                        .build())
                    .toList())
                .build())
            .needsAttention(returnedProtocol.getMarkedForAttention())
            .lastStatusUpdateTimestamp(returnedProtocol.getLastStatusUpdateTimestamp())
            .status(returnedProtocol.getStatus().name())
            .nextInstance(RecurrenceDetailsDto.builder()
                .recurrenceType(returnedProtocol.getRecurrenceType().name())
                .triggeringStatus(returnedProtocol.getTriggeringStatus() == null ? null
                    : returnedProtocol.getTriggeringStatus().name())
                .willReoccurInYears(returnedProtocol.getReoccurInYears())
                .willReoccurInMonths(returnedProtocol.getReoccurInMonths())
                .willReoccurInDays(returnedProtocol.getReoccurInDays())
                .build())
            .completionPercentage(
                ProtocolCalculationHelperService.getProtocolCompletionPercentage(returnedProtocol))

            .associatedSteps(
                AssociatedStepsListDto.builder()
                    .steps(returnedProtocol.getProtocolSteps().stream()
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
                                protocolStep.getCategory().getStepTemplateCategory().getName())
                            .stepTemplateId(protocolStep.getTemplate().getId())
                            .build())
                        .collect(Collectors.toList()))
                    .build())
            .build());
  }

  public ResponseEntity<ErrorMessage> generateFailureResponse(Long protocolId) {
    log.info("Protocol with ID [{}] not found", protocolId);
    return new ResponseEntity<>(
        ErrorMessage.builder()
            .path(PATH)
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Protocol with ID [" + protocolId + "] not found")
            .build(),
        HttpStatus.NOT_FOUND
    );
  }

}
