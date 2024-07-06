package com.cairn.waypoint.dashboard.service.helper;

import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.ExpectedResponseDto;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.UpdateExpectedResponseDetailsDto;
import com.cairn.waypoint.dashboard.service.data.ExpectedResponseDataService;
import java.util.List;

@SuppressWarnings("DuplicatedCode")
public class HomeworkQuestionHelperService {

  private ExpectedResponseDataService expectedResponseDataService;

  public HomeworkQuestionHelperService(ExpectedResponseDataService expectedResponseDataService) {
    this.expectedResponseDataService = expectedResponseDataService;
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
