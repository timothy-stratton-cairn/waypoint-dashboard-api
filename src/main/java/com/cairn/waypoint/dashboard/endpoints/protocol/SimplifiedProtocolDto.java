package com.cairn.waypoint.dashboard.endpoints.protocol;

import com.cairn.waypoint.dashboard.entity.Protocol;

public class SimplifiedProtocolDto {
    private Long id;
    private String name;
    private String description;
    private String status;

    public SimplifiedProtocolDto(Protocol protocol) {
        this.id = protocol.getId();
        this.name = protocol.getName();
        this.description = protocol.getDescription();
        this.status = protocol.getStatus().name();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}