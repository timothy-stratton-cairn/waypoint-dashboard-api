package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtocolCommentDto {

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime takenAt;
  private String takenBy;
  private String comment;
}
