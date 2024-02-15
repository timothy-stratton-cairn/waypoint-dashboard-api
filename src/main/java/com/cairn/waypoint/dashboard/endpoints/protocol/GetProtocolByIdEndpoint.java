package com.cairn.waypoint.dashboard.endpoints.protocol;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.AssociatedUsersListDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolDetailsDto;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolUser;
import com.cairn.waypoint.dashboard.service.ProtocolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
public class GetProtocolByIdEndpoint {
    public static final String PATH = "/api/protocol/{protocolId}";

    private final ProtocolService protocolService;

    public GetProtocolByIdEndpoint(ProtocolService protocolService) {
        this.protocolService = protocolService;
    }

    @GetMapping(PATH)
    @PreAuthorize("hasAuthority('SCOPE_protocol.read')")
    public ResponseEntity<?> getProtocolById(@PathVariable Long protocolId) {
        log.info("Retrieving Protocol with ID [{}]", protocolId);

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
