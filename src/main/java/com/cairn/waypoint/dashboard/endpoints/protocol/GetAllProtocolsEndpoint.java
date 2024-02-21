package com.cairn.waypoint.dashboard.endpoints.protocol;

import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolListDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.mapper.ProtocolMapper;
import com.cairn.waypoint.dashboard.service.ProtocolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class GetAllProtocolsEndpoint {
    public static final String PATH = "/api/protocol";

    private final ProtocolService protocolService;

    public GetAllProtocolsEndpoint(ProtocolService protocolService) {
        this.protocolService = protocolService;
    }

    @GetMapping(PATH)
    @PreAuthorize("hasAuthority('SCOPE_protocol.read')")
    public ResponseEntity<ProtocolListDto> getAllProtocols() {
        return ResponseEntity.ok(
            ProtocolListDto.builder()
                .protocols(
                    this.protocolService.getAllProtocols().stream()
                        .map(ProtocolMapper.INSTANCE::toDto)
                        .toList())
                .build()
        );
    }

}
