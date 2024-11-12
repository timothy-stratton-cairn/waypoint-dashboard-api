package com.cairn.waypoint.dashboard.endpoints.HomeworkQuestionLinkedProtocolTemplate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
class AddHomeworkQuestionLinkedProtocolTemplateDto {
    private Long protocolTemplateId;
    private Long homeworkQuestionId;
}