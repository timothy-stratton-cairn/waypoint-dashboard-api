package com.cairn.waypoint.dashboard.endpoints.HomeworkQuestionLinkedProtocolTemplate;

class AddHomeworkQuestionLinkedProtocolTemplateDto {
    private Long protocolTemplateId;
    private Long homeworkQuestionId;

    public Long getProtocolTemplateId() {
        return protocolTemplateId;
    }

    public void setProtocolTemplateId(Long protocolTemplateId) {
        this.protocolTemplateId = protocolTemplateId;
    }

    public Long getHomeworkQuestionId() {
        return homeworkQuestionId;
    }

    public void setHomeworkQuestionId(Long homeworkQuestionId) {
        this.homeworkQuestionId = homeworkQuestionId;
    }
}