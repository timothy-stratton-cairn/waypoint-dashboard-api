package com.cairn.waypoint.dashboard.service.helper;

import org.springframework.stereotype.Service;

@Service
public class HomeworkHelperService {
/*
  @Value("${waypoint.dashboard.base-url}")
  private String baseUrl;

  public HomeworkDto generateHomeworkDto(Homework homework) {
    return HomeworkDto.builder()
        .homeworkId(homework.getId())
        .name(homework.getName())
        .description(homework.getDescription())
        .parentProtocolId(
        .assignedHouseholdId(homework.getAssignedHouseholdId())
        .homeworkQuestions(HomeworkQuestionListDto.builder()
            .questions(homework.getHomeworkQuestions().stream()
                .map(homeworkResponse -> HomeworkQuestionDto.builder()
                    .questionId(homeworkResponse.getHomeworkQuestion().getId())
                    .questionAbbr(
                        homeworkResponse.getHomeworkQuestion().getQuestionAbbreviation())
                    .question(homeworkResponse.getHomeworkQuestion().getQuestion())
                    .userResponse(homeworkResponse.getHomeworkQuestion().getQuestionType().equals(
                        QuestionTypeEnum.FILE) ? homeworkResponse.getFileGuid() == null ? null
                        : baseUrl + DownloadHomeworkResponseEndpoint.PATH
                            .replace("{fileGuid}", homeworkResponse.getFileGuid()) :
                        homeworkResponse.getResponse())
                    .isRequired(homeworkResponse.getHomeworkQuestion().getRequired())
                    .questionType(
                        homeworkResponse.getHomeworkQuestion().getQuestionType().name())
                    .expectedHomeworkResponses(
                        homeworkResponse.getHomeworkQuestion().getQuestionType()
                            .equals(QuestionTypeEnum.SELECT_OPTION) ||
                            homeworkResponse.getHomeworkQuestion().getQuestionType()
                                .equals(QuestionTypeEnum.MULTI_SELECT_OPTION) ?
                            getExpectedResponseListDto(homeworkResponse.getHomeworkQuestion()
                                .getExpectedHomeworkResponses()) :
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
      Set<ExpectedResponse> expectedResponses) {
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
  */
}
