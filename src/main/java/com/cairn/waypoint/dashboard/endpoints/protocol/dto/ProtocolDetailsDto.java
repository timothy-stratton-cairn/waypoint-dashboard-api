package com.cairn.waypoint.dashboard.endpoints.protocol.dto;


import com.cairn.waypoint.dashboard.dto.authorization.serializer.BigDecimalSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtocolDetailsDto {

  private Long id;
  private String name;
  private String description;
  private String goal;
  private String goalProgress;
  private LocalDateTime createdAt;
  private LocalDate dueBy;
  private LocalDate completedOn;
  private Boolean needsAttention;
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime lastStatusUpdateTimestamp;
  private String status;
  private RecurrenceDetailsDto nextInstance;
  private Long assignedHouseholdId;
  private Long userId;

  @JsonSerialize(using = BigDecimalSerializer.class)
  private BigDecimal completionPercentage;
  private ProtocolCommentListDto protocolComments;
  private AssociatedStepsListDto associatedSteps;
}
