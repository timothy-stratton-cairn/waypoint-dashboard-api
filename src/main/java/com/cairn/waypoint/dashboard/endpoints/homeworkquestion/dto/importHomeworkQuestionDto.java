package com.cairn.waypoint.dashboard.endpoints.homeworkquestion.dto;

import com.cairn.waypoint.dashboard.entity.ExpectedResponse;
import com.cairn.waypoint.dashboard.entity.HomeworkCategory;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.enumeration.QuestionTypeEnum;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class importHomeworkQuestionDto {


  private HomeworkCategory category; // Homework Name. Column B 1
  private String description; // Homework Description
  private Boolean multipleResponses;  //Can Have Multiple Responses Column D 3
  private String abbreviation; // HomeworkQuestion Abbreviation Column E.  4
  private String question;  //Homework Question. Column F. 5
  private QuestionTypeEnum questionType; // Homework Question Type Column G. 6
  private Boolean isRequired; // is Required Column H.  7
  private String expectedResponse; // Expected Response (comma Seperated list) Column I. 8
  //private ExpectedResponse triggeringResponse; // Triggering Response
  private Boolean triggersProtocolCreation; // Triggers Protocol Creation
  private Boolean active;
  //private ProtocolTemplate protocolTemplate; //triggered Protocol

}
