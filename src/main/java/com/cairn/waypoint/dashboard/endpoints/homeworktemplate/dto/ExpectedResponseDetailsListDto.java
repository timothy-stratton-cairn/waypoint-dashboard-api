package com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpectedResponseDetailsListDto {

  private List<ExpectedResponseDetailsDto> responses;
  private Integer numOfResponses;

  public Integer getNumOfResponses() {
    return responses.size();
  }
}
