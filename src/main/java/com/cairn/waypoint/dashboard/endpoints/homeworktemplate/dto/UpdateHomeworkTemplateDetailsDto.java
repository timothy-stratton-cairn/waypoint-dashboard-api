package com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto;

import com.cairn.waypoint.dashboard.entity.enumeration.TemplateStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
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
  private String status;

  private LinkedHashSet<Long> homeworkQuestionIds;

  @JsonIgnore
  public TemplateStatusEnum getTemplateStatus() {
    try {
      return TemplateStatusEnum.valueOf(status);
    } catch (NullPointerException | IllegalArgumentException e) {
      return null;
    }
  }
}
