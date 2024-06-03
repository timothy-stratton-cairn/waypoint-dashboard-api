package com.cairn.waypoint.dashboard.endpoints.protocolstep;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.protocolstep.dto.AppendProtocolStepNoteDto;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolStep;
import com.cairn.waypoint.dashboard.entity.ProtocolStepNote;
import com.cairn.waypoint.dashboard.entity.enumeration.ProtocolCommentTypeEnum;
import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolStepDataService;
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
@Tag(name = "Protocol Step")
public class AppendNoteToProtocolStepEndpoint {

  public static final String PATH = "/api/protocol/{protocolId}/protocol-step/{protocolStepId}/note";

  private final ProtocolDataService protocolDataService;
  private final ProtocolStepDataService protocolStepDataService;

  public AppendNoteToProtocolStepEndpoint(ProtocolDataService protocolDataService,
      ProtocolStepDataService protocolStepDataService) {
    this.protocolDataService = protocolDataService;
    this.protocolStepDataService = protocolStepDataService;
  }

  @Transactional
  @PostMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Allows a user to add notes to a protocol step.",
      description = "Allows a user to add notes to a protocol step. Requires the `protocol.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = String.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> appendCommentOnProtocol(@PathVariable Long protocolId,
      @PathVariable Long protocolStepId,
      @RequestBody AppendProtocolStepNoteDto appendProtocolStepNoteDto, Principal principal) {
    log.info("User [{}] is appending a note to Protocol Step with ID [{}] on Protocol with ID [{}]",
        principal.getName(), protocolStepId, protocolId);

    Optional<Protocol> optionalProtocolToUpdate = this.protocolDataService.getProtocolById(
        protocolId);
    Optional<ProtocolStep> optionalProtocolStepToUpdate = this.protocolStepDataService.getProtocolStepById(
        protocolStepId);

    if (optionalProtocolToUpdate.isEmpty()) {
      return generateFailureResponse("Protocol with ID [" +
          protocolId + "] not found", HttpStatus.NOT_FOUND);
    } else if (optionalProtocolStepToUpdate.isEmpty()) {
      return generateFailureResponse("Protocol Step with ID [" +
          protocolStepId + "] not found", HttpStatus.NOT_FOUND);
    } else if (!optionalProtocolToUpdate.get().getProtocolSteps()
        .contains(optionalProtocolStepToUpdate.get())) {
      return generateFailureResponse("The Protocol Step ID [" + protocolStepId
              + "] is not a Protocol Step ID present on Protocol with ID [" + protocolId + "]",
          HttpStatus.UNPROCESSABLE_ENTITY);
    } else {
      try {
        if (appendProtocolStepNoteDto.getNote() != null &&
            appendProtocolStepNoteDto.getNoteType() != null) {
          appendProtocolStepNoteDto.setProtocolStepNoteType(ProtocolCommentTypeEnum
              .valueOf(appendProtocolStepNoteDto.getNoteType()));
        } else if (appendProtocolStepNoteDto.getNote() != null) {
          appendProtocolStepNoteDto.setProtocolStepNoteType(ProtocolCommentTypeEnum.COMMENT);
        }
      } catch (IllegalArgumentException e) {
        return generateFailureResponse("Provided comment type [" +
                appendProtocolStepNoteDto.getNoteType() + "] does not exist",
            HttpStatus.BAD_REQUEST);
      }
      ProtocolStep protocolStepToUpdate = optionalProtocolStepToUpdate.get();

      protocolStepToUpdate.getNotes().add(ProtocolStepNote.builder()
          .modifiedBy(principal.getName())
          .originalCommenter(principal.getName())
          .note(appendProtocolStepNoteDto.getNote())
          .noteType(appendProtocolStepNoteDto.getProtocolStepNoteType())
          .protocolStep(protocolStepToUpdate)
          .build());

      Long updatedProtocolStepId = this.protocolStepDataService.saveProtocolStep(
          protocolStepToUpdate).getId();

      log.info("Protocol Step with ID [{}] on Protocol with ID [{}] updated with provided note",
          updatedProtocolStepId, protocolId);

      return ResponseEntity.ok("Protocol Step with ID [" +
          updatedProtocolStepId + "] on Protocol with ID [" + protocolId
          + "] updated successfully with commentary [" +
          appendProtocolStepNoteDto.getNote() + "]");
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
