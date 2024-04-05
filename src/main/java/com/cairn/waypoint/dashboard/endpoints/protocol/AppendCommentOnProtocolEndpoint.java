package com.cairn.waypoint.dashboard.endpoints.protocol;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.AppendProtocolCommentaryDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolDetailsDto;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolCommentary;
import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Protocol")
public class AppendCommentOnProtocolEndpoint {


  public static final String PATH = "/api/protocol/{protocolId}/comment";

  private final ProtocolDataService protocolDataService;

  public AppendCommentOnProtocolEndpoint(ProtocolDataService protocolDataService) {
    this.protocolDataService = protocolDataService;
  }

  @Transactional
  @PostMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Allows a user to add to the commentary of a protocol.",
      description = "Allows a user to add to the commentary of a protocol. Requires the `protocol.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ProtocolDetailsDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> appendCommentOnProtocol(@PathVariable Long protocolId,
      @RequestBody AppendProtocolCommentaryDto appendProtocolCommentaryDto, Principal principal) {
    log.info("User [{}] is appending a comment to Protocol with ID [{}]", principal.getName(),
        protocolId);

    Optional<Protocol> optionalProtocolToUpdate = this.protocolDataService.getProtocolById(protocolId);

    if (optionalProtocolToUpdate.isEmpty()) {
      return generateFailureResponse("Protocol with ID [" +
          protocolId + "] not found", HttpStatus.NOT_FOUND);
    } else {
      Protocol protocolToUpdate = updateProtocolFields(optionalProtocolToUpdate.get(),
          appendProtocolCommentaryDto, principal.getName());

      Protocol updatedProtocol = this.protocolDataService.updateProtocol(protocolToUpdate);

      log.info("Protocol with ID [{}] updated with provided commentary", updatedProtocol.getId());

      return ResponseEntity.ok("Protocol with ID [" +
          updatedProtocol.getId() + "] updated successfully with commentary [" +
          appendProtocolCommentaryDto.getComment() + "] and goal progress [" +
          appendProtocolCommentaryDto.getGoalProgress() + "]");
    }
  }

  private Protocol updateProtocolFields(Protocol protocolToUpdate,
      AppendProtocolCommentaryDto appendProtocolCommentaryDto, String modifiedBy) {

    if (appendProtocolCommentaryDto.getComment() != null) {
      protocolToUpdate.getComments().add(ProtocolCommentary.builder()
          .modifiedBy(modifiedBy)
          .originalCommenter(modifiedBy)
          .comment(appendProtocolCommentaryDto.getComment())
          .protocol(protocolToUpdate)
          .build());
    }

    if (appendProtocolCommentaryDto.getGoalProgress() != null) {
      if (protocolToUpdate.getGoalProgress() != null) {
        protocolToUpdate.getComments().add(ProtocolCommentary.builder()
            .modifiedBy(modifiedBy)
            .originalCommenter(modifiedBy)
            .comment("[DEPRECATED GOAL PROGRESS] " + protocolToUpdate.getGoalProgress())
            .protocol(protocolToUpdate)
            .active(Boolean.FALSE)
            .build());
      }

      protocolToUpdate.setGoalProgress(appendProtocolCommentaryDto.getGoalProgress());
    }

    return protocolToUpdate;
  }

  private ResponseEntity<ErrorMessage> generateFailureResponse(String message, HttpStatus status) {
    log.warn(message);
    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    return new ResponseEntity<>(
        ErrorMessage.builder()
            .path(PATH)
            .timestamp(LocalDateTime.now())
            .status(status.value())
            .error(message)
            .build(),
        status
    );
  }
}
