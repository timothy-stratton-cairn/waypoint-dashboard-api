package com.cairn.waypoint.dashboard.endpoints.homeworkquestion;

import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;

import java.time.LocalDateTime;

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

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}