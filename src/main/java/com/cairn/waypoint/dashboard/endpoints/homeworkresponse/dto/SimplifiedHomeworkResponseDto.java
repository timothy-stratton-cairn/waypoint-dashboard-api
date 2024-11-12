package com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimplifiedHomeworkResponseDto {

  private Long id;
  private LocalDateTime updated;
  private String response;
}
