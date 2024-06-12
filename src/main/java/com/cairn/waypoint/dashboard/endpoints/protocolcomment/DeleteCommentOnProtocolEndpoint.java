package com.cairn.waypoint.dashboard.endpoints.protocolcomment;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.entity.Protocol;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Protocol Comment")
public class DeleteCommentOnProtocolEndpoint {


  public static final String PATH = "/api/protocol/{protocolId}/comment/{commentId}";

  private final ProtocolDataService protocolDataService;

  public DeleteCommentOnProtocolEndpoint(ProtocolDataService protocolDataService) {
    this.protocolDataService = protocolDataService;
  }

  @Transactional
  @DeleteMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Allows a user to delete/deactivate commentary of a protocol.",
      description = "Allows a user to delete/deactivate commentary of a protocol. Requires the `protocol.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = String.class))}),
          @ApiResponse(responseCode = "400", description = "Bad Request",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> deleteCommentOnProtocol(@PathVariable Long protocolId,
      @PathVariable Long commentId, Principal principal) {
    log.info("User [{}] is deactivating Protocol Comment with ID [{}] on Protocol with ID [{}]",
        principal.getName(), commentId,
        protocolId);

    Optional<Protocol> optionalProtocolToUpdate = this.protocolDataService.getProtocolById(
        protocolId);

    if (optionalProtocolToUpdate.isEmpty()) {
      return generateFailureResponse("Protocol with ID [" +
          protocolId + "] not found", HttpStatus.NOT_FOUND);
    } else if (optionalProtocolToUpdate.get().getComments().stream()
        .noneMatch(protocolCommentary -> protocolCommentary.getId().equals(commentId))) {
      return generateFailureResponse("Protocol Comment with ID [" +
          commentId + "] not found", HttpStatus.NOT_FOUND);
    } else {
      Protocol protocolToUpdate = optionalProtocolToUpdate.get();

      protocolToUpdate.getComments().stream()
          .filter(protocolCommentary -> protocolCommentary.getId().equals(commentId))
          .forEach(protocolCommentary -> protocolCommentary.setActive(Boolean.FALSE));

      Protocol updatedProtocol = this.protocolDataService.updateProtocol(protocolToUpdate);

      log.info("Protocol Commentary with ID [{}] successfully deactivated",
          updatedProtocol.getId());

      return ResponseEntity.ok("Protocol Commentary with ID [" + commentId + "]"
          + " on Protocol with ID [" + protocolId + "] successfully deactivated");
    }
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
