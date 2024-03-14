package com.cairn.waypoint.dashboard.endpoints.steptemplatecategory.mapper;

import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.AddStepTemplateDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.StepTemplateDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.UpdateStepTemplateDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplatecategory.dto.TemplateCategoryDto;
import com.cairn.waypoint.dashboard.entity.StepTemplate;
import com.cairn.waypoint.dashboard.entity.TemplateCategory;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TemplateCategoryMapper {

  TemplateCategoryMapper INSTANCE = Mappers.getMapper(TemplateCategoryMapper.class);

  TemplateCategoryDto toDto(TemplateCategory templateCategory);
}
