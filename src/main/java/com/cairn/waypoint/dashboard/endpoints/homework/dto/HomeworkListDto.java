package com.cairn.waypoint.dashboard.endpoints.homework.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomeworkListDto {

  private List<HomeworkDto> homeworks;
  private Integer numOfHomeworks;

  public Integer getNumOfHomeworks() {
    return homeworks.size();
  }
}
