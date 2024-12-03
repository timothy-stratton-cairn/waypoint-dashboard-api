package com.cairn.waypoint.dashboard.endpoints.protocol;

import com.cairn.waypoint.dashboard.endpoints.filedownload.DownloadStepAttachmentEndpoint;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.AccountProtocolDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.AccountProtocolListDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.AssociatedStepsListDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolCommentDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolCommentListDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolStepAttachmentDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolStepAttachmentListDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolStepDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolStepNoteDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolStepNoteListDto;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.mapper.AccountProtocolMapper;
import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
import com.cairn.waypoint.dashboard.service.helper.ProtocolCalculationHelperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Protocol")
public class GetAllProtocolsByUserIdEndpoint {

  public static final String PATH = "/api/protocol/user/{userId}";
  private final ProtocolDataService protocolDataService;
  private final AccountProtocolMapper accountProtocolMapper;

  @Value("${waypoint.dashboard.base-url}")
  private String baseUrl;

  public GetAllProtocolsByUserIdEndpoint(ProtocolDataService protocolDataService, AccountProtocolMapper accountProtocolMapper) {
    this.protocolDataService = protocolDataService;
    this.accountProtocolMapper = accountProtocolMapper;
  }

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieve all protocols associated with the given user ID",
      description = "Fetches all protocols that are linked to the specified user ID.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200", description = "Successful retrieval of protocols",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = Protocol.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(schema = @Schema(hidden = true))})
      })
  public ResponseEntity<AccountProtocolListDto> getProtocolsByUserId(@PathVariable Long userId) {
    log.info("Fetching all protocols for user ID [{}]", userId);
    List<Protocol> protocols = protocolDataService.getByUserId(userId);
    List<AccountProtocolDto> protocolDtos = protocols.stream().map(accountProtocolMapper::toAccountProtocolDto).toList();

    AccountProtocolListDto accountProtocolListDto = AccountProtocolListDto.builder()
        .protocols(protocolDtos)
        .build();
    return ResponseEntity.ok(accountProtocolListDto);
  }
}
