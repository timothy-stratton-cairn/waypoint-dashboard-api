package com.cairn.waypoint.dashboard.endpoints.protocol;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.UpdateProtocolStatusDto;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.enumeration.ProtocolStatusEnum;
import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
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
@Tag(name = "Protocol")
public class UpdateProtocolStatusEndpoint {

  public static final String PATH = "/api/protocol/{protocolId}/status";

  private final ProtocolDataService protocolDataService;

  public UpdateProtocolStatusEndpoint(ProtocolDataService protocolDataService) {
    this.protocolDataService = protocolDataService;
  }

  @Transactional
  @PatchMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Allows a user to update a protocol's status.",
      description = "Updates a protocol's status. Requires the `protocol.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              description = "Updated - Protocol status update was successful"),
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
  public ResponseEntity<?> updateProtocolById(@PathVariable Long protocolId,
      @RequestBody UpdateProtocolStatusDto updateProtocolStatusDto, Principal principal) {
    log.info("User [{}] is updating the status of Protocol with ID [{}]", principal.getName(),
        protocolId);

    Optional<Protocol> optionalProtocolToUpdate = this.protocolDataService.getProtocolById(
        protocolId);

    if (optionalProtocolToUpdate.isEmpty()) {
      return generateFailureResponse("Protocol with ID [" +
          protocolId + "] not found", HttpStatus.NOT_FOUND);
    } else {
      return updateProtocolStatus(optionalProtocolToUpdate.get(), updateProtocolStatusDto,
          principal.getName());
    }
  }

  private ResponseEntity<?> updateProtocolStatus(Protocol protocolToUpdate,
      UpdateProtocolStatusDto updateProtocolDetailsDto, String modifiedBy) {
    Protocol updatedProtocol;
    try {
      protocolToUpdate.setModifiedBy(modifiedBy);
      protocolToUpdate.setStatus(ProtocolStatusEnum
          .valueOf(updateProtocolDetailsDto.getNewProtocolStatus()));

      if (ProtocolStatusEnum
          .valueOf(updateProtocolDetailsDto.getNewProtocolStatus())
          == ProtocolStatusEnum.COMPLETED) {
        protocolToUpdate.setCompletionDate(LocalDate.now());
      }

      updatedProtocol = protocolDataService.updateProtocol(protocolToUpdate);

      return ResponseEntity.status(HttpStatus.OK)
          .body("Protocol with ID [" + updatedProtocol.getId() + "] and name ["
              + updatedProtocol.getName() + "] updated successfully");
    } catch (IllegalArgumentException e) {
      return generateFailureResponse("Provided Protocol Status [ " +
              updateProtocolDetailsDto.getNewProtocolStatus() + "does not exist",
          HttpStatus.BAD_REQUEST);
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
