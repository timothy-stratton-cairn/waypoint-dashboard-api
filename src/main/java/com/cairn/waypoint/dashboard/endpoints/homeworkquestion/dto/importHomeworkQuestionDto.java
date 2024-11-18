package com.cairn.waypoint.dashboard.endpoints.homeworkquestion.dto;

import com.cairn.waypoint.dashboard.entity.ExpectedResponse;
import com.cairn.waypoint.dashboard.entity.HomeworkCategory;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.enumeration.QuestionTypeEnum;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class importHomeworkQuestionDto {


  private HomeworkCategory category; // Homework Name
  private String description; // Homework Description
  private Boolean multipleResponses;  //Can Have Multiple Responses
  private String abbreviation; // HomeworkQuestion Abbreviation
  private String question;  //Homework Question
  private QuestionTypeEnum questionType; // Homework Question Type
  private Boolean isRequired; // is Required
  private String expectedResponse; // Expected Response (comma Seperated list)
  private ExpectedResponse triggeringResponse; // Triggering Response
  private Boolean triggersProtocolCreation; // Triggers Protocol Creation
  private Protocol protocol; //triggered Protocol


}
