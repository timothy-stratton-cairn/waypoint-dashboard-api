package com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto;

import com.cairn.waypoint.dashboard.endpoints.protocoltemplate.dto.ProtocolTemplateDetailsDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomeworkQuestionDetailsDto {

  private String questionAbbreviation;
  private String question;
  private String questionType;
  private Boolean isRequired;
  private ExpectedResponseDetailsListDto expectedHomeworkResponses;
  private Boolean triggersProtocolCreation;
  private ExpectedResponseDetailsDto triggeringResponse;
  private TriggeredProtocolTemplateDetailsDto triggeredProtocol;
}
