package com.cairn.waypoint.dashboard.service.helper;

import com.cairn.waypoint.dashboard.endpoints.homework.dto.HomeworkQuestionDto;
import com.cairn.waypoint.dashboard.endpoints.homework.dto.HomeworkQuestionDto.HomeworkQuestionDtoBuilder;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.ExpectedResponseDto;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.UpdateExpectedResponseDetailsDto;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.service.data.ExpectedResponseDataService;
import java.util.List;

@SuppressWarnings("DuplicatedCode")
public class HomeworkQuestionHelperService {

  private ExpectedResponseDataService expectedResponseDataService;

  public HomeworkQuestionHelperService(ExpectedResponseDataService expectedResponseDataService) {
    this.expectedResponseDataService = expectedResponseDataService;
  }

  public HomeworkQuestionDto generateHomeworkQuestionDto(HomeworkQuestion question) {
	  return HomeworkQuestionDto.builder()
			  .questionId(question.getId())
			  .questionAbbr(question.getQuestionAbbreviation())
			  .question(question.getQuestion())
			  .questionType(question.getQuestionType().name())
			  .categoryId(question.getCategory().getId())
			  .isRequired(question.getRequired())
			  .active(question.getActive())
			  .triggeredProtocol(question.getTriggeredProtocol())
			  .triggersProtocolCreation(question.getTriggersProtocolCreation())
			  .userResponse(question.getUserResponse())
			  .build();
  }
  public static boolean getIsValidTriggerRequest(
      Boolean triggerProtocolCreation, Long triggeredProtocolId,
      List<ExpectedResponseDto> responseOptions, ExpectedResponseDto triggeringResponse) {
    if (!triggerProtocolCreation) {
      return true;
    } else if (triggeredProtocolId == null) {
      return false;
    } else if (responseOptions != null && responseOptions.isEmpty()) {
      return false;
    } else {
      return responseOptions != null && triggeringResponse != null
          && responseOptions.contains(triggeringResponse);
    }
  }
 

  public static boolean getIsValidTriggerRequest(
      Boolean triggerProtocolCreation, Long triggeredProtocolId,
      List<UpdateExpectedResponseDetailsDto> responseOptions,
      UpdateExpectedResponseDetailsDto triggeringResponse) {
    if (!triggerProtocolCreation) {
      return true;
    } else if (triggeredProtocolId == null) {
      return false;
    } else if (responseOptions != null && responseOptions.isEmpty()) {
      return false;
    } else {
      return responseOptions != null && triggeringResponse != null
          && responseOptions.contains(triggeringResponse);
    }
  }
}
