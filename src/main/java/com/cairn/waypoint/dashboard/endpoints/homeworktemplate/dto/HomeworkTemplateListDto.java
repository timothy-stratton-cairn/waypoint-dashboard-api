package com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomeworkTemplateListDto {

  private List<HomeworkTemplateDto> homeworkTemplates;
  private Integer numOfHomeworkTemplates;

  public Integer getNumOfHomeworkTemplates() {
    return homeworkTemplates.size();
  }
}
