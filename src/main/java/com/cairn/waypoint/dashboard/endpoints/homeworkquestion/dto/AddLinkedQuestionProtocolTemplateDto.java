package com.cairn.waypoint.dashboard.endpoints.homeworkquestion.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class AddLinkedQuestionProtocolTemplateDto {
	
	 @NotNull
	    private Long templateId;

	    @NotNull
	    private Long questionId;

	    private Boolean active = true; // Default value

}
