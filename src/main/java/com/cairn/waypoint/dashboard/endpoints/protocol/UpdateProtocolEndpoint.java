package com.cairn.waypoint.dashboard.endpoints.protocol;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.UpdateProtocolDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.UpdateProtocolStepDetailsDto;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolCommentary;
import com.cairn.waypoint.dashboard.entity.ProtocolStep;
import com.cairn.waypoint.dashboard.entity.ProtocolStepNote;
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
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Protocol")
public class UpdateProtocolEndpoint {

  public static final String PATH = "/api/protocol/{protocolId}";

  private final ProtocolDataService protocolDataService;
  private final ProtocolStepDataService protocolStepDataService;

  public UpdateProtocolEndpoint(ProtocolDataService protocolDataService,
      ProtocolStepDataService protocolStepDataService) {
    this.protocolDataService = protocolDataService;
    this.protocolStepDataService = protocolStepDataService;
  }

  @Transactional
  @PatchMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Allows a user to update a protocol.",
      description = "Updates a protocol. Requires the `protocol.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              description = "Updated - Protocol update was successful"),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> updateProtocolById(@PathVariable Long protocolId,
      @RequestBody UpdateProtocolDetailsDto updateProtocolDetailsDto, Principal principal) {
    log.info("User [{}] is updating Protocol with ID [{}]", principal.getName(), protocolId);

    Optional<Protocol> optionalProtocolToUpdate = this.protocolDataService.getProtocolById(
        protocolId);

    if (optionalProtocolToUpdate.isEmpty()) {
      return generateFailureResponse("Protocol with ID [" +
          protocolId + "] not found", HttpStatus.NOT_FOUND);
    } else {
      return updateProtocol(optionalProtocolToUpdate.get(), updateProtocolDetailsDto,
          principal.getName());
    }
  }

  private ResponseEntity<?> updateProtocol(Protocol protocolToUpdate,
      UpdateProtocolDetailsDto updateProtocolDetailsDto, String modifiedBy) {
    Protocol updatedProtocol = this.protocolDataService.updateProtocol(
        setUpdatedProtocolProperties(protocolToUpdate, updateProtocolDetailsDto, modifiedBy));

    if (updateProtocolDetailsDto.getProtocolSteps() != null) {
      for (UpdateProtocolStepDetailsDto updatedProtocolStep : updateProtocolDetailsDto.getProtocolSteps()) {
        Optional<ProtocolStep> optionalProtocolStepToUpdate = updatedProtocol.getProtocolSteps()
            .stream()
            .filter(protocolStep -> Objects.equals(protocolStep.getId(),
                updatedProtocolStep.getId()))
            .findFirst();

        if (optionalProtocolStepToUpdate.isEmpty()) {
          return generateFailureResponse("Protocol Step with ID [" +
              updatedProtocolStep.getId() + "] not found associated with Protocol with ID [" +
              updatedProtocol.getId() + "]", HttpStatus.NOT_FOUND);
        } else {
          updatedProtocol = updateProtocolStep(updatedProtocol, optionalProtocolStepToUpdate.get(),
              updatedProtocolStep, modifiedBy,
              updatedProtocol);
        }
      }
    }

    return ResponseEntity.status(HttpStatus.OK)
        .body("Protocol with ID [" + updatedProtocol.getId() + "] and name ["
            + updatedProtocol.getName() + "] updated successfully");
  }

  private Protocol setUpdatedProtocolProperties(Protocol protocolToUpdate,
      UpdateProtocolDetailsDto updateProtocolDetailsDto, String modifiedBy) {
    protocolToUpdate.setModifiedBy(modifiedBy);

    if (updateProtocolDetailsDto.getComment() != null) {
      protocolToUpdate.getComments()
          .forEach(protocolCommentary -> protocolCommentary.setActive(Boolean.FALSE));
      protocolToUpdate.getComments().add(ProtocolCommentary.builder()
          .modifiedBy(modifiedBy)
          .originalCommenter(modifiedBy)
          .comment(updateProtocolDetailsDto.getComment())
          .protocol(protocolToUpdate)
          .build());
    }

    if (updateProtocolDetailsDto.getMarkForAttention() != null) {
      protocolToUpdate.setMarkedForAttention(updateProtocolDetailsDto.getMarkForAttention());
    }

    if (updateProtocolDetailsDto.getGoal() != null) {
      protocolToUpdate.setGoal(updateProtocolDetailsDto.getGoal());
    }

    if (updateProtocolDetailsDto.getGoalProgress() != null) {
      protocolToUpdate.setGoalProgress(updateProtocolDetailsDto.getGoalProgress());
    }

    if (updateProtocolDetailsDto.getDueDate() != null) {
      protocolToUpdate.setDueDate(updateProtocolDetailsDto.getDueDate());
    }

    return protocolToUpdate;
  }

  private Protocol updateProtocolStep(Protocol updatedProtocol, ProtocolStep protocolStepToUpdate,
      UpdateProtocolStepDetailsDto updatedProtocolStep, String modifiedBy,
      Protocol protocolToUpdate) {
    protocolStepToUpdate.setModifiedBy(modifiedBy);
    if (updatedProtocolStep.getNotes() != null) {
      protocolStepToUpdate.getNotes()
          .forEach(protocolStepNote -> protocolStepNote.setActive(Boolean.FALSE));
      protocolStepToUpdate.getNotes().add(ProtocolStepNote.builder()
          .modifiedBy(modifiedBy)
          .originalCommenter(modifiedBy)
          .note(updatedProtocolStep.getNotes())
          .protocolStep(protocolStepToUpdate)
          .build());
    }

    if (updatedProtocolStep.getStatus() != null) {
      protocolStepToUpdate.setStatus(StepStatusEnum.valueOf(updatedProtocolStep.getStatus()));

      //Since a Protocol Step was updated we should update the last update timestamp
      updatedProtocol.setLastStatusUpdateTimestamp(LocalDateTime.now());

      if (updatedProtocol.getProtocolSteps().stream()
          .allMatch(protocolStep -> protocolStep.getStatus().equals(StepStatusEnum.DONE))) {
        updatedProtocol.setCompletionDate(LocalDate.now());
      } else {
        updatedProtocol.setCompletionDate(null);
      }

      updatedProtocol = this.protocolDataService.updateProtocol(updatedProtocol);
    }
    Long protocolStepId = this.protocolStepDataService.saveProtocolStep(protocolStepToUpdate)
        .getId();

    log.info("Updated Protocol Step with ID [{}] on Protocol with ID [{}]", protocolStepId,
        protocolToUpdate.getId());

    return updatedProtocol;
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
