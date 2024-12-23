package com.cairn.waypoint.dashboard.endpoints.homework.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomeworkQuestionDto {

  private Long questionId;
  private String questionAbbr;
  private String question;
  private String userResponse;
  private Boolean isRequired;
  private Boolean active;
  private String questionType;
  private Long categoryId;
  private Boolean triggersProtocolCreation;
  private ProtocolTemplateDto triggeredProtocol;
  private ExpectedResponseListDto expectedHomeworkResponses;
}
