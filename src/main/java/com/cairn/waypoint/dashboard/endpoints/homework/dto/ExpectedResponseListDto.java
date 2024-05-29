package com.cairn.waypoint.dashboard.endpoints.homework.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpectedResponseListDto {

  private List<ExpectedResponseDto> responses;
  private Integer numOfResponses;

  public Integer getNumOfResponses() {
    if (responses == null) {
      return 0;
    }
    return responses.size();
  }

}
