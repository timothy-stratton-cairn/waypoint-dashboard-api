package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtocolStepDto {

  private Long id;
  private String name;
  private String description;
  private String status;
  private String category;
  private Long stepTemplateId;
  private LinkedHomeworksDto linkedHomeworks;
  private ProtocolStepNoteListDto stepNotes;
}
