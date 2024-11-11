package com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomeworkResponseDto{
	private Long responseId;
	private Long questionId;
	private Long categoryId;
	private Long userId;
	private String response;
	private String fileGuid;


	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public Long getResponseId() {
		return responseId;
	}

	public void setResponseId(Long responseId) {
		this.responseId = responseId;
	}

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
}