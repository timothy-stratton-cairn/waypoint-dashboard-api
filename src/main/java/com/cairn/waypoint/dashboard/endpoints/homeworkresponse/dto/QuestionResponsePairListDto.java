package com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto;

import java.util.ArrayList;
import java.util.List;
//TODO Needs to be refactored to be more efficient
public class QuestionResponsePairListDto {
    private List<QuestionResponsePairDto> questions;
    private Integer numberOfPairs;


    public QuestionResponsePairListDto(List<QuestionResponsePairDto> questions) {
        this.questions = questions;
        this.numberOfPairs = questions != null ? questions.size() : 0;
    }

    public List<QuestionResponsePairDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionResponsePairDto> questions) {
        this.questions = questions;
        this.numberOfPairs = questions != null ? questions.size() : 0;
    }

    public Integer getNumberOfPairs() {
        return numberOfPairs;
    }
}
