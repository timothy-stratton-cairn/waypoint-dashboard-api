package com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto;

import com.cairn.waypoint.dashboard.endpoints.homeworkquestion.SimplifiedHomeworkQuestionDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.SimplifiedProtocolDto;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.HomeworkResponse;
import com.cairn.waypoint.dashboard.entity.Protocol;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class QuestionResponsePairDto {
    private SimplifiedProtocolDto protocol;
    private SimplifiedHomeworkQuestionDto question;
    private SimplifiedHomeworkResponseDto response;

    public QuestionResponsePairDto(Protocol protocol, HomeworkQuestion question, HomeworkResponse response) {
        this.protocol = new SimplifiedProtocolDto(protocol);
        this.question = new SimplifiedHomeworkQuestionDto(question);
        this.response = response != null ? new SimplifiedHomeworkResponseDto(response) : null;
    }

    public SimplifiedProtocolDto getProtocol() {
        return protocol;
    }

    public SimplifiedHomeworkQuestionDto getQuestion() {
        return question;
    }

    public SimplifiedHomeworkResponseDto getResponse() {
        return response;
    }

}
