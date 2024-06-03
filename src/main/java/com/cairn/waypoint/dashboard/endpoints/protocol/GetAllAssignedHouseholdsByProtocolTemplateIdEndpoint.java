package com.cairn.waypoint.dashboard.endpoints.protocol;

import com.cairn.waypoint.dashboard.dto.HouseholdAccountListDto;
import com.cairn.waypoint.dashboard.dto.HouseholdDto;
import com.cairn.waypoint.dashboard.dto.HouseholdListDto;
import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolTemplateGroupedAccountDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolTemplateGroupedAccountsListDto;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.service.data.HouseholdDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolTemplateDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Protocol")
public class GetAllAssignedHouseholdsByProtocolTemplateIdEndpoint {

  public static final String PATH = "/api/protocol/household/protocol-template/{protocolTemplateId}";

  private final ProtocolDataService protocolDataService;
  private final ProtocolTemplateDataService protocolTemplateDataService;
  private final HouseholdDataService householdDataService;

  public GetAllAssignedHouseholdsByProtocolTemplateIdEndpoint(
      ProtocolDataService protocolDataService,
      ProtocolTemplateDataService protocolTemplateDataService,
      HouseholdDataService householdDataService) {
    this.protocolDataService = protocolDataService;
    this.protocolTemplateDataService = protocolTemplateDataService;
    this.householdDataService = householdDataService;
  }

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieves all protocols by the provided protocol template ID.",
      description = "Retrieves all protocols by the provided protocol template ID. Requires the `protocol.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ProtocolTemplateGroupedAccountsListDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> getAllAccountsAssociatedToProtocolTemplateID(
      @PathVariable Long protocolTemplateId, Principal principal) {
    log.info(
        "User [{}] is Retrieving All Users assigned to Protocols created from Protocol Template with ID [{}]",
        principal.getName(), protocolTemplateId);
    if (this.protocolTemplateDataService.getProtocolTemplateById(protocolTemplateId).isEmpty()) {
      return generateFailureResponse("Protocol Template with ID [" +
          protocolTemplateId + "] not found", HttpStatus.NOT_FOUND);
    } else if (this.protocolDataService.getByProtocolTemplateId(protocolTemplateId).isEmpty()) {
      return generateFailureResponse("No protocols created from Protocol Template with ID [" +
          protocolTemplateId + "]", HttpStatus.NOT_FOUND);
    } else {

      List<Long> householdIds = this.protocolDataService.getByProtocolTemplateId(protocolTemplateId)
          .stream()
          .map(Protocol::getAssignedHouseholdId)
          .toList();

      HouseholdListDto accountListDto = this.householdDataService.getHouseholdDetailsListByIdList(
          householdIds);

      return ResponseEntity.ok(
          ProtocolTemplateGroupedAccountsListDto.builder()
              .accounts(accountListDto.getHouseholds().stream()
                  .map(HouseholdDto::getHouseholdAccounts)
                  .map(HouseholdAccountListDto::getAccounts)
                  .flatMap(List::stream)
                  .map(accountDto -> ProtocolTemplateGroupedAccountDto.builder()
                      .id(accountDto.getId())
                      .firstName(accountDto.getFirstName())
                      .lastName(accountDto.getLastName())
                      .build())
                  .collect(Collectors.toList()))
              .build()
      );
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
