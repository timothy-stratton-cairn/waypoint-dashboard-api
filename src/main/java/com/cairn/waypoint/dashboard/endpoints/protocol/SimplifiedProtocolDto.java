package com.cairn.waypoint.dashboard.endpoints.protocol;

import com.cairn.waypoint.dashboard.entity.Protocol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SimplifiedProtocolDto {
    private Long id;
    private String name;
    private String description;
    private String status;
}