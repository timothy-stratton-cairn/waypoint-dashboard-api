package com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class HomeworkResponseDto{
	private Long responseId;
	private Long questionId;
	private Long categoryId;
	private Long userId;
	private String response;
	private String fileGuid;
}