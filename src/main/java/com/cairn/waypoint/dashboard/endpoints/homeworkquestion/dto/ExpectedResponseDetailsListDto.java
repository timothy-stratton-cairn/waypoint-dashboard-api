package com.cairn.waypoint.dashboard.endpoints.homeworkquestion.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpectedResponseDetailsListDto {

  private List<ExpectedResponseDetailsDto> responses;
  private Integer numOfResponses;

  public Integer getNumOfResponses() {
    if (responses == null) {
      return 0;
    }
    return responses.size();
  }

}
