package com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateHomeworkTemplateDetailsDto {

  @Size(max = 32, message = "The Homework Template name cannot exceed 32 characters in length")
  private String name;
  @Size(max = 500, message = "The Homework Template description cannot exceed 500 characters in length")
  private String description;

  private Boolean isMultiResponse;

  @Valid
  private List<UpdateHomeworkQuestionDetailsDto> homeworkQuestions;
}
