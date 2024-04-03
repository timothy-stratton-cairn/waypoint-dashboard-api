package com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
public class AddHomeworkTemplateDetailsDto {

  @Size(max = 32, message = "The Question Abbreviation cannot exceed 32 characters in length")
  @NotNull(message = "Question Abbreviation cannot be null")
  private String name;
  @Size(max = 500, message = "The Question cannot exceed 500 characters in length")
  private String description;

  @NotNull(message = "Question Abbreviation cannot be null")
  private Boolean isMultiResponse;

  @Valid
  private List<AddHomeworkQuestionDetailsDto> homeworkQuestions;
}
