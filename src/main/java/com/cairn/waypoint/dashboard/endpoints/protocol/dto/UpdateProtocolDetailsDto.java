package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import com.cairn.waypoint.dashboard.entity.enumeration.ProtocolCommentTypeEnum;
import com.cairn.waypoint.dashboard.entity.enumeration.ProtocolStatusEnum;
import com.cairn.waypoint.dashboard.entity.enumeration.RecurrenceTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateProtocolDetailsDto {

  private String protocolName;
  private String goal;
  private String goalProgress;
  private String comment;
  private String commentType;
  private Boolean markForAttention;
  private LocalDate dueDate;
  private String recurrenceTypeValue;
  private String triggeringStatusValue;
  private Integer reoccurInYears;
  private Integer reoccurInMonths;
  private Integer reoccurInDays;
  private List<UpdateProtocolStepDetailsDto> protocolSteps;

  @JsonIgnore
  private ProtocolCommentTypeEnum protocolCommentType;

  @JsonIgnore
  public RecurrenceTypeEnum getRecurrenceType() {
    if (recurrenceTypeValue != null) {
      return RecurrenceTypeEnum.valueOf(recurrenceTypeValue);
    } else {
      return null;
    }
  }

  @JsonIgnore
  public ProtocolStatusEnum getTriggeringStatus() {
    if (triggeringStatusValue != null) {
      return ProtocolStatusEnum.valueOf(triggeringStatusValue);
    } else {
      return null;
    }
  }
}
