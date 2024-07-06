package com.cairn.waypoint.dashboard.service.helper;

import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.ExpectedResponseDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.ExpectedResponseDetailsListDto;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.HomeworkQuestionDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.HomeworkQuestionDetailsListDto;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.HomeworkTemplateDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.TriggeredProtocolTemplateDetailsDto;
import com.cairn.waypoint.dashboard.entity.ExpectedResponse;
import com.cairn.waypoint.dashboard.entity.HomeworkTemplate;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import java.util.Objects;
import java.util.Set;

public class HomeworkTemplateHelperService {

  public static HomeworkTemplateDetailsDto getHomeworkTemplateDetailsDto(
      HomeworkTemplate homeworkTemplate) {
    return
        com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.HomeworkTemplateDetailsDto.builder()
            .id(homeworkTemplate.getId())
            .name(homeworkTemplate.getName())
            .description(homeworkTemplate.getDescription())
            .status(homeworkTemplate.getStatus().name())
            .isMultiResponse(homeworkTemplate.getMultiResponse())
            .homeworkQuestions(HomeworkQuestionDetailsListDto.builder()
                .questions(homeworkTemplate.getHomeworkQuestions().stream()
                    .map(
                        homeworkTemplateLinkedHomeworkQuestion -> HomeworkQuestionDetailsDto.builder()
                            .questionId(homeworkTemplateLinkedHomeworkQuestion.getHomeworkQuestion()
                                .getId())
                            .questionAbbreviation(
                                homeworkTemplateLinkedHomeworkQuestion.getHomeworkQuestion()
                                    .getQuestionAbbreviation())
                            .question(
                                homeworkTemplateLinkedHomeworkQuestion.getHomeworkQuestion()
                                    .getQuestion())
                            .questionType(
                                homeworkTemplateLinkedHomeworkQuestion.getHomeworkQuestion()
                                    .getQuestionType().name())
                            .isRequired(
                                homeworkTemplateLinkedHomeworkQuestion.getHomeworkQuestion()
                                    .getRequired())
                            .expectedHomeworkResponses(
                                HomeworkTemplateHelperService.getExpectedResponseDetailsListDto(
                                    homeworkTemplateLinkedHomeworkQuestion.getHomeworkQuestion()
                                        .getExpectedHomeworkResponses()))
                            .triggersProtocolCreation(
                                homeworkTemplateLinkedHomeworkQuestion.getHomeworkQuestion()
                                    .getTriggersProtocolCreation())
                            .triggeringResponse(
                                HomeworkTemplateHelperService.getExpectedResponseDetailsDto(
                                    homeworkTemplateLinkedHomeworkQuestion.getHomeworkQuestion()
                                        .getTriggeringResponse()))
                            .triggeredProtocol(HomeworkTemplateHelperService.getTriggeredProtocol(
                                homeworkTemplateLinkedHomeworkQuestion.getHomeworkQuestion()
                                    .getTriggeredProtocol()))
                            .status(homeworkTemplateLinkedHomeworkQuestion.getHomeworkQuestion().getStatus().name())
                            .build())
                    .toList())
                .build())
            .build();
  }

  private static ExpectedResponseDetailsListDto getExpectedResponseDetailsListDto(
      Set<ExpectedResponse> expectedResponses) {
    if (expectedResponses == null || expectedResponses.isEmpty()) {
      return null;
    } else {
      return ExpectedResponseDetailsListDto.builder()
          .responses(expectedResponses.stream()
              .map(expectedResponse -> ExpectedResponseDetailsDto.builder()
                  .response(expectedResponse.getResponse())
                  .tooltip(expectedResponse.getTooltip())
                  .build())
              .toList())
          .build();
    }
  }

  private static ExpectedResponseDetailsDto getExpectedResponseDetailsDto(
      ExpectedResponse expectedResponse) {
    if (Objects.isNull(expectedResponse)) {
      return null;
    } else {
      return ExpectedResponseDetailsDto.builder()
          .response(expectedResponse.getResponse())
          .tooltip(expectedResponse.getTooltip())
          .build();
    }
  }

  private static TriggeredProtocolTemplateDetailsDto getTriggeredProtocol(
      ProtocolTemplate protocolTemplate) {
    if (Objects.isNull(protocolTemplate)) {
      return null;
    } else {
      return TriggeredProtocolTemplateDetailsDto.builder()
          .id(protocolTemplate.getId())
          .name(protocolTemplate.getName())
          .description(protocolTemplate.getDescription())
          .build();
    }
  }
}
