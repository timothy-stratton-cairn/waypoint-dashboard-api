package com.cairn.waypoint.dashboard.endpoints.protocol;

import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/protocols")
@Tag(name = "Protocol", description = "Endpoints for managing protocols")
public class GetAllProtocolsByUserIdEndpoint {

    private final ProtocolDataService protocolDataService;

    public GetAllProtocolsByUserIdEndpoint(ProtocolDataService protocolDataService) {
        this.protocolDataService = protocolDataService;
    }

    @GetMapping("/user/{userId}")
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
    public ResponseEntity<List<Protocol>> getProtocolsByUserId(@PathVariable Long userId) {
        log.info("Fetching all protocols for user ID [{}]", userId);
        List<Protocol> protocols = protocolDataService.findByUserId(userId);
        return ResponseEntity.ok(protocols);
    }
}
