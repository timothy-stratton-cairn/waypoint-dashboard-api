package com.cairn.waypoint.dashboard.endpoints.homeworkquestion.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomeworkQuestionDetailsDto {

  private Long questionId;
  private String questionAbbr;
  private String question;
  private String status;
  private Boolean isRequired;
  private String questionType;
  private Boolean triggersProtocolCreation;
  private ProtocolTemplateDetailsDto triggeredProtocol;
  private ExpectedResponseDetailsListDto expectedHomeworkResponses;
  private ExpectedResponseDetailsDto triggeringResponse;
}
