package com.cairn.waypoint.dashboard.endpoints.recurrencetype.dto;

import com.cairn.waypoint.dashboard.endpoints.stepstatus.dto.StepStatusDto;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecurrenceTypeListDto {

  private List<RecurrenceTypeDto> recurrenceTypes;
  private Integer numOfRecurrenceTypes;

  public Integer getNumOfRecurrenceTypes() {
    return recurrenceTypes.size();
  }
}
