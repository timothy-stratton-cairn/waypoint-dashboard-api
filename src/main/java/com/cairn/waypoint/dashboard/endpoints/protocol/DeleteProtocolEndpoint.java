package com.cairn.waypoint.dashboard.endpoints.protocol;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Protocol")
public class DeleteProtocolEndpoint {

  public static final String PATH = "/api/protocol/{protocolId}";

  private final ProtocolDataService protocolDataService;

  public DeleteProtocolEndpoint(ProtocolDataService protocolDataService) {
    this.protocolDataService = protocolDataService;
  }

  @Transactional
  @DeleteMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Allows a user to deactivate a protocol.",
      description = "Deactivates a protocol. Requires the `protocol.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                  schema = @Schema(implementation = String.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> deleteProtocolById(@PathVariable Long protocolId, Principal principal) {
    log.info("User [{}] is deactivating Protocol with ID [{}]", principal.getName(), protocolId);

    final ResponseEntity<?>[] response = new ResponseEntity<?>[1];
    this.protocolDataService.getProtocolById(protocolId)
        .ifPresentOrElse(
            returnedProtocol -> response[0] = generateSuccessResponse(returnedProtocol,
                principal.getName()),
            () -> response[0] = generateFailureResponse(protocolId)
        );

    return response[0];
  }

  public ResponseEntity<String> generateSuccessResponse(
      Protocol protocolToDelete, String modifiedBy) {
    protocolToDelete.setActive(Boolean.FALSE);
    protocolToDelete.setModifiedBy(modifiedBy);

    protocolToDelete.getComments().forEach(comment -> {
      comment.setActive(Boolean.FALSE);
      comment.setModifiedBy(modifiedBy);
    });
    protocolToDelete.getProtocolSteps().forEach(step -> {
      step.getNotes().forEach(note -> {
        note.setActive(Boolean.FALSE);
        note.setModifiedBy(modifiedBy);
      });
        /*step.getLinkedHomework().forEach(homeworkLink -> {
        homeworkLink.setActive(Boolean.FALSE);
        homeworkLink.setModifiedBy(modifiedBy);
      });*/

      step.setActive(Boolean.FALSE);
      step.setModifiedBy(modifiedBy);
    });

    protocolDataService.updateProtocol(protocolToDelete);

    log.info("Protocol with ID [{}] has been deactivated", protocolToDelete.getId());
    return ResponseEntity.ok(
        "Protocol with ID [" + protocolToDelete.getId() + "] has been deactivated");
  }

  public ResponseEntity<ErrorMessage> generateFailureResponse(Long homeworkId) {
    log.warn("Protocol with ID [{}] not found", homeworkId);
    return new ResponseEntity<>(
        ErrorMessage.builder()
            .path(PATH)
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Protocol with ID [" + homeworkId + "] not found")
            .build(),
        HttpStatus.NOT_FOUND
    );
  }

}
