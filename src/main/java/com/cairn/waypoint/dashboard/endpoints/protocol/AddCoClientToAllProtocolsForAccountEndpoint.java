package com.cairn.waypoint.dashboard.endpoints.protocol;

import com.cairn.waypoint.dashboard.endpoints.protocol.dto.AccountProtocolDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.AccountProtocolListDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.AssociatedStepsListDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolCommentDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolCommentListDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolStepDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolStepNoteDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolStepNoteListDto;
import com.cairn.waypoint.dashboard.entity.ProtocolUser;
import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolUserDataService;
import com.cairn.waypoint.dashboard.service.helper.ProtocolCalculationHelperService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Protocol")
public class AddCoClientToAllProtocolsForAccountEndpoint {

  public static final String PATH = "/api/protocol/account/{accountId}/{coClientId}";

  private final ProtocolDataService protocolDataService;
  private final ProtocolUserDataService protocolUserDataService;

  public AddCoClientToAllProtocolsForAccountEndpoint(ProtocolDataService protocolDataService,
      ProtocolUserDataService protocolUserDataService) {
    this.protocolDataService = protocolDataService;
    this.protocolUserDataService = protocolUserDataService;
  }

  @PostMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Updates all protocols assigned to the provided account with the provided coClient.",
      description =
          "Updates all protocols assigned to the provided account with the provided coClient."
              + " Requires the `protocol.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = AccountProtocolListDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(schema = @Schema(hidden = true))})})
  public ResponseEntity<AccountProtocolListDto> getAllProtocols(@PathVariable Long accountId,
      @PathVariable Long coClientId, Principal principal) {
    log.info(
        "User [{}] is Updating All Protocols associated with Account ID [{}] with Co-Client ID [{}]",
        principal.getName(), accountId, coClientId);

    protocolUserDataService.getAllProtocolsForAccountId(accountId)
        .forEach(protocolUser -> {
          ProtocolUser newProtocolUser = ProtocolUser.builder()
              .modifiedBy(principal.getName())
              .protocol(protocolUser.getProtocol())
              .userId(coClientId)
              .build();

          protocolUserDataService.saveProtocolUser(newProtocolUser);
        });

    return ResponseEntity.ok(
        AccountProtocolListDto.builder()
            .protocols(
                this.protocolDataService.getByUserId(accountId).stream()
                    .map(protocol ->
                        AccountProtocolDto.builder()
                            .id(protocol.getId())
                            .name(protocol.getName())
                            .description(protocol.getDescription())
                            .goal(protocol.getGoal())
                            .goalProgress(protocol.getGoalProgress())
                            .createdAt(protocol.getCreated())
                            .dueBy(protocol.getDueDate())
                            .completedOn(protocol.getCompletionDate())
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
                                ProtocolCalculationHelperService.getProtocolCompletionPercentage(
                                    protocol))
                            .associatedSteps(
                                AssociatedStepsListDto.builder()
                                    .steps(protocol.getProtocolSteps().stream()
                                        .map(protocolStep -> ProtocolStepDto.builder()
                                            .id(protocolStep.getId())
                                            .name(protocolStep.getName())
                                            .description(protocolStep.getDescription())
                                            .stepNotes(ProtocolStepNoteListDto.builder()
                                                .notes(protocolStep.getNotes().stream()
                                                    .map(
                                                        protocolStepNote -> ProtocolStepNoteDto.builder()
                                                            .takenAt(protocolStepNote.getCreated())
                                                            .takenBy(
                                                                protocolStepNote.getOriginalCommenter())
                                                            .note(protocolStepNote.getNote())
                                                            .build())
                                                    .toList())
                                                .build())
                                            .status(
                                                protocolStep.getStatus().getInstance().getName())
                                            .linkedHomeworkId(
                                                protocolStep.getLinkedHomework() != null ?
                                                    protocolStep.getLinkedHomework().getId() : null)
                                            .category(
                                                protocolStep.getCategory().getTemplateCategory()
                                                    .getName())
                                            .build())
                                        .collect(Collectors.toList()))
                                    .build())
                            .build())
                    .toList())
            .build());
  }
}
