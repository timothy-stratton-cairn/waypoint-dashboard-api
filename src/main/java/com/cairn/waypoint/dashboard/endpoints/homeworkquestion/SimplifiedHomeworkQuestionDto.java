package com.cairn.waypoint.dashboard.endpoints.homeworkquestion;

import com.cairn.waypoint.dashboard.entity.enumeration.QuestionTypeEnum;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimplifiedHomeworkQuestionDto {

  private Long id;
  private LocalDateTime updated;
  private Long categoryId;
  private QuestionTypeEnum questionType;
  private String question;
  private String label;

}