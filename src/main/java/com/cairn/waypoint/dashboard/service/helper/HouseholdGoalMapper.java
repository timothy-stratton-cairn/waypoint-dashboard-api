package com.cairn.waypoint.dashboard.service.helper;

import com.cairn.waypoint.dashboard.endpoints.householdgoals.dto.AddHouseholdGoalDto;
import com.cairn.waypoint.dashboard.endpoints.householdgoals.dto.HouseholdGoalDto;
import com.cairn.waypoint.dashboard.entity.HouseholdGoal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper
public interface HouseholdGoalMapper {
  HouseholdGoalMapper INSTANCE = Mappers.getMapper(HouseholdGoalMapper.class);

  @Mapping(source = "goalTemplate.goalCategory.name", target = "category")
  HouseholdGoalDto toDto(HouseholdGoal entity);

  HouseholdGoal toEntity(AddHouseholdGoalDto dto);

  List<HouseholdGoalDto> toDtoList(List<HouseholdGoal> householdGoals);
}

