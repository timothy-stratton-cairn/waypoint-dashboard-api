package com.cairn.waypoint.dashboard.endpoints.steptemplate.dto;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.cairn.waypoint.dashboard.entity.StepTemplateLinkedHomeworkTemplate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomeworkTemplateDetailsDto {

  private Long id;
  private String name;

}

@Mapper
interface StepTemplateLinkedHomeworkTemplateMapper {

    StepTemplateLinkedHomeworkTemplateMapper INSTANCE = Mappers.getMapper(StepTemplateLinkedHomeworkTemplateMapper.class);

    HomeworkTemplateDetailsDto toDto(StepTemplateLinkedHomeworkTemplate entity);
}
