package com.cairn.waypoint.dashboard.endpoints.steptemplate.mapper;

import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.AddStepTemplateDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.HomeworkTemplateDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.HomeworkTemplateDetailsListDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.StepTemplateDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.StepTemplateDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.UpdateStepTemplateDetailsDto;
import com.cairn.waypoint.dashboard.entity.StepTemplate;
import com.cairn.waypoint.dashboard.entity.StepTemplateLinkedHomeworkTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StepTemplateMapper {

  StepTemplateMapper INSTANCE = Mappers.getMapper(StepTemplateMapper.class);
  
  StepTemplateDto toDto(StepTemplate stepTemplate);
  StepTemplate toEntity(AddStepTemplateDetailsDto addStepTemplateDetailsDto);
  
  //@Mapping(target = "linkedHomeworkTemplates", source = "stepTemplateLinkedHomeworks")
  @Mapping(target ="linkedStepTask", source = "linkedTask")
  StepTemplateDetailsDto toDetailsDto(StepTemplate stepTemplate);
  
  @Mapping(target = "status", source = "templateStatus")
  StepTemplate toEntity(
      UpdateStepTemplateDetailsDto updateStepTemplateDetailsDto);
  
  
 /* @Mapping(target = "homeworkTemplates", source = "stepTemplateLinkedHomeworks")
  default HomeworkTemplateDetailsListDto map(Set<StepTemplateLinkedHomeworkTemplate> stepTemplateLinkedHomeworks) {
	  if (stepTemplateLinkedHomeworks == null || stepTemplateLinkedHomeworks.isEmpty()) {
	        return null; 
	    }


      List<HomeworkTemplateDetailsDto> homeworkTemplates = stepTemplateLinkedHomeworks.stream() //convert each item of the set stepTemplateLinkedHomeworks into a HomeWorkTemplateDetailsDto 
          .map(this::toHomeworkTemplateDetailsDto) 
          .collect(Collectors.toList()); //collects each of those dtos and puts them in the list. 

      return HomeworkTemplateDetailsListDto.builder()
          .homeworkTemplates(homeworkTemplates)
          .numOfHomeworkTemplates(homeworkTemplates.size())
          .build();
  }
  @Mapping(target = "name", source = "homeworkTemplate.name")
  HomeworkTemplateDetailsDto toHomeworkTemplateDetailsDto(StepTemplateLinkedHomeworkTemplate entity);*/
  
}
