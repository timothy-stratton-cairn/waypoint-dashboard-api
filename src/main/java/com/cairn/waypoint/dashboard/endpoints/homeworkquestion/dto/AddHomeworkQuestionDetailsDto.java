package com.cairn.waypoint.dashboard.endpoints.homeworkquestion.dto;

import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.ExpectedResponseDto;
import com.cairn.waypoint.dashboard.entity.enumeration.QuestionTypeEnum;
import com.cairn.waypoint.dashboard.service.helper.HomeworkQuestionHelperService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
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
public class AddHomeworkQuestionDetailsDto {

  @Size(max = 32, message = "The Question Abbreviation cannot exceed 32 characters in length")
  @NotNull(message = "Question Abbreviation cannot be null")
  private String questionAbbr;

  @Size(max = 500, message = "The Question cannot exceed 500 characters in length")
  @NotNull(message = "Question cannot be null")
  private String question;

  @NotNull(message = "Question Type cannot be null")
  private QuestionTypeEnum questionType;

  private Boolean isRequired;

  @Valid
  private List<ExpectedResponseDto> responseOptions;
  private Boolean triggerProtocolCreation;

  @Valid
  private ExpectedResponseDto triggeringResponse;
  private Long triggeredProtocolId;

  @JsonIgnore
  @AssertTrue(message = "Invalid Request: If this Homework Question triggers Protocol Creation, "
      + "you must provide a Protocol Template ID to be triggered, "
      + "a List of Response Options, "
      + "and a Triggering Response.")
  public boolean isValidTriggeringRequest() {
    return HomeworkQuestionHelperService.getIsValidTriggerRequest(this.triggerProtocolCreation,
        this.triggeredProtocolId,
        this.responseOptions, this.triggeringResponse);
  }

  @JsonIgnore
  @AssertTrue(message =
      "Invalid Request: If Question Type is 'SELECT_OPTION' or 'MULTI_SELECT_OPTION', "
          + "a list of expected responses must be provided.")
  public boolean isValidSelectOptionRequest() {
    return !((this.questionType.equals(QuestionTypeEnum.SELECT_OPTION) || this.questionType.equals(
        QuestionTypeEnum.MULTI_SELECT_OPTION))
        && this.responseOptions.isEmpty());
  }
}
