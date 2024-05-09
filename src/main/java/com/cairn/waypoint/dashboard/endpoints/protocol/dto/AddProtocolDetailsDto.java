package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddProtocolDetailsDto {

  private Long protocolTemplateId;
  private String goal;
  private String comment;
  private Long associatedAccountId;
  private LocalDate dueDate;
}
