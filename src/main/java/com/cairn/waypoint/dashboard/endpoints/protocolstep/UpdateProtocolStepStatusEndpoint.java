package com.cairn.waypoint.dashboard.endpoints.protocolstep;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.protocolstep.dto.UpdateProtocolStepStatusDto;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolStep;
import com.cairn.waypoint.dashboard.entity.ProtocolStepNote;
import com.cairn.waypoint.dashboard.entity.enumeration.ProtocolCommentTypeEnum;
import com.cairn.waypoint.dashboard.entity.enumeration.StepStatusEnum;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Protocol Step")
public class UpdateProtocolStepStatusEndpoint {

  public static final String PATH = "/api/protocol/{protocolId}/protocol-step/{protocolStepId}/status";

  private final ProtocolDataService protocolDataService;
  private final ProtocolStepDataService protocolStepDataService;

  public UpdateProtocolStepStatusEndpoint(ProtocolDataService protocolDataService,
      ProtocolStepDataService protocolStepDataService) {
    this.protocolDataService = protocolDataService;
    this.protocolStepDataService = protocolStepDataService;
  }

  @Transactional
  @PatchMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Allows a user to update the status of a protocol step.",
      description = "Allows a user to update the status of a protocol step. Requires the `protocol.full` permission.",
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
  public ResponseEntity<?> updateStatusOnProtocol(@PathVariable Long protocolId,
      @PathVariable Long protocolStepId,
      @RequestBody UpdateProtocolStepStatusDto updateProtocolStepStatusDto, Principal principal) {
    log.info(
        "User [{}] is updating the status of Protocol Step with ID [{}] on Protocol with ID [{}] to [{}]",
        principal.getName(), protocolStepId, protocolId, updateProtocolStepStatusDto.getStatus());

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
    } else if (updateProtocolStepStatusDto.getStatus().equals(StepStatusEnum.CONDITIONAL_COMPLETION)
        &&
        (updateProtocolStepStatusDto.getCompletionCondition() == null ||
            updateProtocolStepStatusDto.getCompletionCondition().isEmpty())) {
      return generateFailureResponse("A Completion Condition message must be provided"
              + " when a step is updated to the 'Conditional Completion' status",
          HttpStatus.BAD_REQUEST);
    } else {
      ProtocolStep protocolStepToUpdate = optionalProtocolStepToUpdate.get();

      protocolStepToUpdate.setStatus(updateProtocolStepStatusDto.getStatus());
      protocolStepToUpdate.getNotes().add(ProtocolStepNote.builder()
          .modifiedBy(principal.getName())
          .protocolStep(protocolStepToUpdate)
          .note(updateProtocolStepStatusDto.getCompletionCondition())
          .originalCommenter(principal.getName())
          .noteType(ProtocolCommentTypeEnum.CONDITIONAL_COMPLETION_NOTE)
          .build());

      ProtocolStep updatedProtocolStep = this.protocolStepDataService.saveProtocolStep(
          protocolStepToUpdate);

      Protocol protocolToUpdate = updatedProtocolStep.getParentProtocol();
      if (protocolToUpdate.getProtocolSteps().stream()
          .allMatch(protocolStep -> protocolStep.getStatus().equals(StepStatusEnum.DONE) ||
              protocolStep.getStatus().equals(StepStatusEnum.CONDITIONAL_COMPLETION))) {
        protocolToUpdate.setCompletionDate(LocalDate.now());
      } else {
        protocolToUpdate.setCompletionDate(null);
      }
      this.protocolDataService.updateProtocol(protocolToUpdate);

      log.info(
          "Protocol Step with ID [{}] on Protocol with ID [{}] updated with provided status [{}]",
          updatedProtocolStep.getId(), protocolId, updateProtocolStepStatusDto.getStatus());

      return ResponseEntity.ok("Protocol Step with ID [" +
          updatedProtocolStep.getId() + "] on Protocol with ID [" + protocolId
          + "] updated successfully with status [" +
          updateProtocolStepStatusDto.getStatus() + "]");
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
