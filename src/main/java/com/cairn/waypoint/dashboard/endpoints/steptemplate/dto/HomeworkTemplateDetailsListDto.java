package com.cairn.waypoint.dashboard.endpoints.steptemplate.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomeworkTemplateDetailsListDto {

  private List<HomeworkTemplateDetailsDto> homeworkTemplates;
  private Integer numOfHomeworkTemplates;

  public Integer getNumOfHomeworkTemplates() {
    if (homeworkTemplates == null) {
      return 0;
    }
    return homeworkTemplates.size();
  }
}
