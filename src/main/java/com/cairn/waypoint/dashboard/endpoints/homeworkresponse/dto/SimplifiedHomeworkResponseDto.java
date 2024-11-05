package com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto;

import com.cairn.waypoint.dashboard.entity.HomeworkResponse;

import java.time.LocalDateTime;

public class SimplifiedHomeworkResponseDto {
    private Long id;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    private LocalDateTime updated;
    private String response;

    public SimplifiedHomeworkResponseDto(HomeworkResponse response) {
        this.id = response.getId();
        this.updated = response.getUpdated();
        this.response = response.getResponse();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}