package com.cairn.waypoint.dashboard.endpoints.ops.dto;

import com.cairn.waypoint.dashboard.entity.enumeration.QuestionTypeEnum;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomeworkSheetEntryDto {

  private String homeworkName;
  private String homeworkDescription;
  private Boolean canHaveMultipleResponses;
  private String homeworkQuestionAbbreviation;
  private String homeworkQuestion;
  private QuestionTypeEnum homeworkQuestionType;
  private Boolean isRequired;
  private List<String> expectedResponses;
  private String triggeringResponse;
  private Boolean triggersProtocolCreation;
  private String triggeredProtocol;
}
