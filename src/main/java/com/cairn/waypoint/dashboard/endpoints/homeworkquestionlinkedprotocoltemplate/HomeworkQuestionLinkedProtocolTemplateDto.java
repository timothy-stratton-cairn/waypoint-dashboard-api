package com.cairn.waypoint.dashboard.endpoints.homeworkquestionlinkedprotocoltemplate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomeworkQuestionLinkedProtocolTemplateDto {

  private Long id;
  private Long protocolTemplateId;
  private Long homeworkQuestionId;
}