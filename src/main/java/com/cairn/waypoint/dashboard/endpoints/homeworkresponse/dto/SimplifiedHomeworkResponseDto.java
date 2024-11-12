package com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto;

import com.cairn.waypoint.dashboard.entity.HomeworkResponse;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimplifiedHomeworkResponseDto {
    private Long id;
    private LocalDateTime updated;
    private String response;

    public SimplifiedHomeworkResponseDto(HomeworkResponse response) {
        this.id = response.getId();
        this.updated = response.getUpdated();
        this.response = response.getResponse();
    }
}
