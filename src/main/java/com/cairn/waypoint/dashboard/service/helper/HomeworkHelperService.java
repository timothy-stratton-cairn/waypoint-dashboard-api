package com.cairn.waypoint.dashboard.service.helper;

import com.cairn.waypoint.dashboard.endpoints.filedownload.DownloadFileEndpoint;
import com.cairn.waypoint.dashboard.endpoints.homework.dto.ExpectedResponseDto;
import com.cairn.waypoint.dashboard.endpoints.homework.dto.ExpectedResponseListDto;
import com.cairn.waypoint.dashboard.endpoints.homework.dto.HomeworkDto;
import com.cairn.waypoint.dashboard.endpoints.homework.dto.HomeworkQuestionDto;
import com.cairn.waypoint.dashboard.endpoints.homework.dto.HomeworkQuestionListDto;
import com.cairn.waypoint.dashboard.endpoints.homework.dto.HomeworkUserListDto;
import com.cairn.waypoint.dashboard.endpoints.homework.dto.ProtocolTemplateDto;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.ExpectedResponseDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.ExpectedResponseDetailsListDto;
import com.cairn.waypoint.dashboard.entity.ExpectedResponse;
import com.cairn.waypoint.dashboard.entity.Homework;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.ProtocolUser;
import com.cairn.waypoint.dashboard.entity.enumeration.QuestionTypeEnum;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HomeworkHelperService {

  @Value("${waypoint.dashboard.base-url}")
  private String baseUrl;

  public HomeworkDto generateHomeworkDto(Homework homework) {
    return HomeworkDto.builder()
        .homeworkId(homework.getId())
        .name(homework.getName())
        .description(homework.getDescription())
        .parentProtocolId(
            homework.getAssociatedProtocolStep().getParentProtocol().getId())
        .parentProtocolStepId(homework.getAssociatedProtocolStep().getId())
        .assignedUsers(HomeworkUserListDto.builder()
            .userIds(homework.getAssociatedProtocolStep().getParentProtocol().getAssociatedUsers()
                .stream().map(
                    ProtocolUser::getUserId).toList()).build())
        .homeworkQuestions(HomeworkQuestionListDto.builder()
            .questions(homework.getHomeworkQuestions().stream()
                .map(homeworkResponse -> HomeworkQuestionDto.builder()
                    .questionId(homeworkResponse.getHomeworkQuestion().getId())
                    .questionAbbr(
                        homeworkResponse.getHomeworkQuestion().getQuestionAbbreviation())
                    .question(homeworkResponse.getHomeworkQuestion().getQuestion())
                    .userResponse(homeworkResponse.getHomeworkQuestion().getQuestionType().equals(
                        QuestionTypeEnum.FILE) ? homeworkResponse.getFileGuid() == null ? null
                        : baseUrl + DownloadFileEndpoint.PATH
                            .replace("{fileGuid}", homeworkResponse.getFileGuid()) :
                        homeworkResponse.getResponse())
                    .isRequired(homeworkResponse.getHomeworkQuestion().getRequired())
                    .questionType(
                        homeworkResponse.getHomeworkQuestion().getQuestionType().name())
                    .expectedHomeworkResponses(homeworkResponse.getHomeworkQuestion().getQuestionType().equals(QuestionTypeEnum.SELECT_OPTION) ||
                        homeworkResponse.getHomeworkQuestion().getQuestionType().equals(QuestionTypeEnum.MULTI_SELECT_OPTION) ?
                        getExpectedResponseListDto(homeworkResponse.getHomeworkQuestion().getExpectedHomeworkResponses()) :
                        null)
                    .triggersProtocolCreation(homeworkResponse.getHomeworkQuestion()
                        .getTriggersProtocolCreation())
                    .triggeredProtocol(
                        getHomeworkTemplateDto(homeworkResponse.getHomeworkQuestion()))
                    .build())
                .toList())
            .build())
        .build();
  }


  private ExpectedResponseListDto getExpectedResponseListDto(
      List<ExpectedResponse> expectedResponses) {
    if (expectedResponses.isEmpty()) {
      return null;
    } else {
      return ExpectedResponseListDto.builder()
          .responses(expectedResponses.stream()
              .map(expectedResponse -> ExpectedResponseDto.builder()
                  .response(expectedResponse.getResponse())
                  .tooltip(expectedResponse.getTooltip())
                  .build())
              .toList())
          .build();
    }
  }

  private ProtocolTemplateDto getHomeworkTemplateDto(HomeworkQuestion homeworkQuestion) {
    if (!homeworkQuestion.getTriggersProtocolCreation()) {
      return null;
    } else if (homeworkQuestion.getTriggeredProtocol() == null) {
      return null;
    } else {
      return ProtocolTemplateDto.builder()
          .id(homeworkQuestion.getTriggeredProtocol().getId())
          .name(homeworkQuestion.getTriggeredProtocol().getName())
          .build();
    }
  }
}
