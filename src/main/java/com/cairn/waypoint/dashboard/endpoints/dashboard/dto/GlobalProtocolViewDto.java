package com.cairn.waypoint.dashboard.endpoints.dashboard.dto;

import com.cairn.waypoint.dashboard.dto.serializer.BigDecimalSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalProtocolViewDto {

  private Long protocolTemplateId;
  private String protocolTemplateName;
  @Builder.Default
  private Long numStepsTodo = 0L;
  @Builder.Default
  private Long numStepsInProgress = 0L;
  @Builder.Default
  private Long numStepsDone = 0L;
  @Builder.Default
  @JsonSerialize(using = BigDecimalSerializer.class)
  private BigDecimal completionPercentage = BigDecimal.ZERO;

  private AssociatedAccountListDto assignedUsers;
}
