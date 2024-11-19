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


  private HomeworkCategory category; 
  private String description;
  private Boolean multipleResponses;
  private String abbreviation;
  private String question;
  private QuestionTypeEnum questionType;
  private Boolean isRequired;
  private String expectedResponse;
  private Boolean triggersProtocolCreation;
  private Boolean active;

}
