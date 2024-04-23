package com.cairn.waypoint.dashboard.service.helper;

import com.cairn.waypoint.dashboard.endpoints.homework.dto.HomeworkDto;
import com.cairn.waypoint.dashboard.endpoints.homework.dto.HomeworkQuestionDto;
import com.cairn.waypoint.dashboard.endpoints.homework.dto.HomeworkQuestionListDto;
import com.cairn.waypoint.dashboard.endpoints.homework.dto.HomeworkUserListDto;
import com.cairn.waypoint.dashboard.endpoints.homework.dto.ProtocolTemplateDto;
import com.cairn.waypoint.dashboard.entity.Homework;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.ProtocolUser;

public class HomeworkHelperService {

  public static HomeworkDto generateHomeworkDto(Homework homework) {
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
                    .userResponse(homeworkResponse.getResponse())
                    .isRequired(homeworkResponse.getHomeworkQuestion().getRequired())
                    .questionType(
                        homeworkResponse.getHomeworkQuestion().getQuestionType().name())
                    .triggersProtocolCreation(homeworkResponse.getHomeworkQuestion()
                        .getTriggersProtocolCreation())
                    .triggeredProtocol(
                        getHomeworkTemplateDto(homeworkResponse.getHomeworkQuestion()))
                    .build())
                .toList())
            .build())
        .build();
  }

  private static ProtocolTemplateDto getHomeworkTemplateDto(HomeworkQuestion homeworkQuestion) {
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
