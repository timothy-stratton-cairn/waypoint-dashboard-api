package com.cairn.waypoint.dashboard.endpoints.protocol.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtocolStepNoteListDto {

  List<ProtocolStepNoteDto> notes;
  private Integer numOfNotes;

  public Integer getNumOfNotes() {
    if (notes == null) {
      return 0;
    }
    return notes.size();
  }
}
