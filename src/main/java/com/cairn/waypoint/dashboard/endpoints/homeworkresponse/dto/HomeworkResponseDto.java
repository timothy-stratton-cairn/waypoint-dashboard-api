package com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomeworkResponseDto {

  private Long responseId;
  private Long questionId;
  private Long userId;
  private String question;
  private String response;
  private String fileGuid;
}