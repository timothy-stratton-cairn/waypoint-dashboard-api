package com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto;

import java.util.ArrayList;
import java.util.List;

public class QuestionResponsePairListDto {
    private ArrayList<QuestionResponsePairDto> questions;
    private Integer numberOfPairs;


    public QuestionResponsePairListDto(ArrayList<QuestionResponsePairDto> questions) {
        this.questions = questions;
        this.numberOfPairs = questions != null ? questions.size() : 0;
    }

    public List<QuestionResponsePairDto> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<QuestionResponsePairDto> questions) {
        this.questions = questions;
        this.numberOfPairs = questions != null ? questions.size() : 0;
    }

    public Integer getNumberOfPairs() {
        return numberOfPairs;
    }
}
