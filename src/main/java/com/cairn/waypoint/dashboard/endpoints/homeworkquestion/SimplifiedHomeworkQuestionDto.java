package com.cairn.waypoint.dashboard.endpoints.homeworkquestion;

import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SimplifiedHomeworkQuestionDto {
    private Long id;
    private LocalDateTime updated;
    private Long categoryId;
    private String question;
    private String label;

    public SimplifiedHomeworkQuestionDto(HomeworkQuestion question) {
        this.id = question.getId();
        this.updated = question.getUpdated();
        this.question = question.getQuestion();
        this.label = question.getLabel();
        this.categoryId = question.getCategory().getId();
    }
}