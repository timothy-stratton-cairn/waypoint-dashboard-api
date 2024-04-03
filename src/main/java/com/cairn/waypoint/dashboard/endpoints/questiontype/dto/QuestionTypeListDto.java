package com.cairn.waypoint.dashboard.endpoints.questiontype.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionTypeListDto {

  private List<QuestionTypeDto> statuses;
  private Integer numOfStatuses;

  public Integer getNumOfStatuses() {
    return statuses.size();
  }
}
