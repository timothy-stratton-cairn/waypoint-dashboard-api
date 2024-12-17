package com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AddHomeworkQuestionResponseDto {

  private Long questionId;
  private Long userId;
  private String response;
  @JsonIgnore
  private String fileGuid;


  public String getFileGuid() {
    return fileGuid;
  }

  public void setFileGuid(String fileGuid) {
    this.fileGuid = fileGuid;
  }

  public Long getQuestionId() {
    return questionId;
  }

  public void setQuestionId(Long questionId) {
    this.questionId = questionId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }

}
