package com.cairn.waypoint.dashboard.endpoints.protocolstatus;

import com.cairn.waypoint.dashboard.endpoints.protocolstatus.dto.ProtocolStatusDto;
import com.cairn.waypoint.dashboard.endpoints.protocolstatus.dto.ProtocolStatusListDto;
import com.cairn.waypoint.dashboard.entity.enumeration.ProtocolStatusEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Protocol Status")
public class GetAllProtocolStatusesEndpoint {

  public static final String PATH = "/api/protocol/status";

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.status.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieves all protocol statuses.",
      description = "Retrieves all protocol statuses. Requires the `protocol.status.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ProtocolStatusListDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(schema = @Schema(hidden = true))})})
  public ResponseEntity<ProtocolStatusListDto> getAllProtocolStatuses(Principal principal) {
    log.info("User [{}] is Retrieving All Protocol Statuses", principal.getName());
    return ResponseEntity.ok(
        ProtocolStatusListDto.builder()
            .statuses(Stream.of(ProtocolStatusEnum.values())
                .map(protocolStatusEnum -> ProtocolStatusDto.builder()
                    .status(protocolStatusEnum.name())
                    .build())
                .toList())
            .build()
    );
  }

}
