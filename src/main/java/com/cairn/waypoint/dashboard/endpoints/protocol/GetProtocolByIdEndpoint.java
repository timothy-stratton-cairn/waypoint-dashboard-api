package com.cairn.waypoint.dashboard.endpoints.protocol;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.AssociatedUsersListDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolDetailsDto;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolUser;
import com.cairn.waypoint.dashboard.service.ProtocolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;

@Slf4j
@RestController
@Tag(name = "Protocol")
public class GetProtocolByIdEndpoint {
    public static final String PATH = "/api/protocol/{protocolId}";

    private final ProtocolService protocolService;

    public GetProtocolByIdEndpoint(ProtocolService protocolService) {
        this.protocolService = protocolService;
    }

    @GetMapping(PATH)
    @PreAuthorize("hasAuthority('SCOPE_protocol.read')")
    @Operation(
            summary = "Retrieves a protocol by it's ID.",
            description = "Retrieves a protocol by it's ID. Requires the `protocol.read` permission.",
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
    public ResponseEntity<?> getProtocolById(@PathVariable Long protocolId, Principal principal) {
        log.info("User [{}} is Retrieving Protocol with ID [{}]", principal.getName(), protocolId);

        final ResponseEntity<?>[] response = new ResponseEntity<?>[1];
        this.protocolService.getProtocolById(protocolId)
            .ifPresentOrElse(
                returnedProtocol -> response[0] = generateSuccessResponse(returnedProtocol),
                () -> response[0] = generateFailureResponse(protocolId)
            );

        return response[0];
    }

    public ResponseEntity<ProtocolDetailsDto> generateSuccessResponse(Protocol returnedProtocol) {
        return ResponseEntity.ok(
            ProtocolDetailsDto.builder()
                .id(returnedProtocol.getId())
                .name(returnedProtocol.getName())
                .associatedUsers(
                    AssociatedUsersListDto.builder()
                        .userIds(returnedProtocol.getAssociatedUsers().stream().map(ProtocolUser::getUserId).toList())
                        .build()
                )
                .build()
        );
    }

    public ResponseEntity<ErrorMessage> generateFailureResponse(Long protocol) {
        log.info("Protocol with ID [{}] not found", protocol);
        return new ResponseEntity<>(
                ErrorMessage.builder()
                        .path(PATH)
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.NOT_FOUND.value())
                        .error("Protocol with ID [" + protocol + "] not found")
                        .build(),
                HttpStatus.NOT_FOUND
        );
    }

}
