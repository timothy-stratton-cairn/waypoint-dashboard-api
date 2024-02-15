package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AssociatedUsersListDto {
    List<Long> userIds;
    private Integer numOfUsers;

    public Integer getNumOfUsers() {
        return userIds.size();
    }
}
