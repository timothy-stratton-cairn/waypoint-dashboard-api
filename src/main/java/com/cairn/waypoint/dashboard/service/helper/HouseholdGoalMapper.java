package com.cairn.waypoint.dashboard.service.helper;

import com.cairn.waypoint.dashboard.endpoints.householdgoals.dto.AddHouseholdGoalDto;
import com.cairn.waypoint.dashboard.endpoints.householdgoals.dto.HouseholdGoalDto;
import com.cairn.waypoint.dashboard.entity.HouseholdGoal;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface HouseholdGoalMapper {
  HouseholdGoalMapper INSTANCE = Mappers.getMapper(HouseholdGoalMapper.class);

  HouseholdGoal toEntity(AddHouseholdGoalDto dto);

  HouseholdGoalDto toDto(HouseholdGoal entity);
}
