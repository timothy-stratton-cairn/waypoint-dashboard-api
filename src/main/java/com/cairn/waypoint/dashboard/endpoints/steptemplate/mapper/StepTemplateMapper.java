package com.cairn.waypoint.dashboard.endpoints.steptemplate.mapper;

import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.AddStepTemplateDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.StepTemplateDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.StepTemplateDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.UpdateStepTemplateDetailsDto;
import com.cairn.waypoint.dashboard.entity.StepTemplate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StepTemplateMapper {

  StepTemplateMapper INSTANCE = Mappers.getMapper(StepTemplateMapper.class);

  StepTemplateDto toDto(StepTemplate stepTemplate);

  StepTemplate toEntity(AddStepTemplateDetailsDto addStepTemplateDetailsDto);

  //@Mapping(target = "linkedHomeworkTemplates", source = "stepTemplateLinkedHomeworks")
  @Mapping(target = "linkedStepTask", source = "linkedTask")
  StepTemplateDetailsDto toDetailsDto(StepTemplate stepTemplate);

  @Mapping(target = "status", source = "templateStatus")
  StepTemplate toEntity(
      UpdateStepTemplateDetailsDto updateStepTemplateDetailsDto);
}
