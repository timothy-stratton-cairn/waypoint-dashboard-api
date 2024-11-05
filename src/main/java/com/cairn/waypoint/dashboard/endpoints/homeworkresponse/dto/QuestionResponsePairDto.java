package com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto;

import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.HomeworkResponse;

public class QuestionResponsePairDto {
    private Long protocol_id;
    private HomeworkQuestion question;
    private HomeworkResponse response;


    public QuestionResponsePairDto(Long protocol_id, HomeworkQuestion question, HomeworkResponse response) {
        this.protocol_id = protocol_id;
        this.question = question;
        this.response = response;
    }

    // Getters and setters
    public Long getProtocolId() {
        return protocol_id;
    }

    public void setProtocolId(Long protocol_id) {
        this.protocol_id = protocol_id;
    }

    public HomeworkQuestion getQuestion() {
        return question;
    }

    public void setQuestion(HomeworkQuestion question) {
        this.question = question;
    }

    public HomeworkResponse getResponse() {
        return response;
    }

    public void setResponse(HomeworkResponse response) {
        this.response = response;
    }
}