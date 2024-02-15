package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProtocolListDto {
    private List<ProtocolDto> protocols;
    private Integer numOfProtocols;

    public Integer getNumOfProtocols() {
        return protocols.size();
    }
}
