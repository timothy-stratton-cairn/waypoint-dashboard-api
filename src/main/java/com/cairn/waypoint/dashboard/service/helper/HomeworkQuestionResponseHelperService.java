package com.cairn.waypoint.dashboard.service.helper;
import com.cairn.waypoint.dashboard.endpoints.homework.dto.UpdateHomeworkResponseDetailsDto;
import com.cairn.waypoint.dashboard.entity.HomeworkResponse;
import com.cairn.waypoint.dashboard.service.data.ExpectedResponseDataService;


@SuppressWarnings("DuplicatedCode")
public class HomeworkQuestionResponseHelperService {

  private ExpectedResponseDataService expectedResponseDataService;

  public HomeworkQuestionResponseHelperService(ExpectedResponseDataService expectedResponseDataService) {
    this.expectedResponseDataService = expectedResponseDataService;
  }
  
  public UpdateHomeworkResponseDetailsDto generateHomeworkQuestionResponseDto(HomeworkResponse response) {
	  return UpdateHomeworkResponseDetailsDto.builder()
			  .questionId(response.getHomework().getId())
			  .userResponse(response.getResponse())
			  .build();
  }
}