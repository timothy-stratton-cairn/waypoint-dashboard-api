package com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto;

import com.cairn.waypoint.dashboard.endpoints.homeworkquestion.SimplifiedHomeworkQuestionDto;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.HomeworkResponse;

public class QuestionResponsePairDto {
    private SimplifiedHomeworkQuestionDto question;
    private SimplifiedHomeworkResponseDto response;

    public QuestionResponsePairDto(HomeworkQuestion question, HomeworkResponse response) {
        this.question = new SimplifiedHomeworkQuestionDto(question);
        this.response = response != null ? new SimplifiedHomeworkResponseDto(response) : null;
    }

    public SimplifiedHomeworkQuestionDto getQuestion() {
        return question;
    }

    public SimplifiedHomeworkResponseDto getResponse() {
        return response;
    }

}
