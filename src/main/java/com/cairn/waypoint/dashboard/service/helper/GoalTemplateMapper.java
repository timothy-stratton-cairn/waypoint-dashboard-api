package com.cairn.waypoint.dashboard.service.helper;

import com.cairn.waypoint.dashboard.endpoints.householdgoalstemplate.dto.AddGoalTemplateDto;
import com.cairn.waypoint.dashboard.endpoints.householdgoalstemplate.dto.HouseholdGoalTemplateDto;
import com.cairn.waypoint.dashboard.entity.GoalTemplate;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GoalTemplateMapper {
  GoalTemplateMapper INSTANCE = Mappers.getMapper(GoalTemplateMapper.class);

  GoalTemplate toEntity(AddGoalTemplateDto dto);

  HouseholdGoalTemplateDto toDto(GoalTemplate entity);
}
