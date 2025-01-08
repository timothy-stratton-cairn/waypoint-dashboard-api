package com.cairn.waypoint.dashboard.service.helper;
import com.cairn.waypoint.dashboard.endpoints.householdgoals.dto.AddHouseholdGoalDto;
import com.cairn.waypoint.dashboard.endpoints.householdgoals.dto.HouseholdGoalDto;
import com.cairn.waypoint.dashboard.entity.HouseholdGoal;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.service.data.HouseholdGoalDataService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomHouseholdGoalMapper implements HouseholdGoalMapper {

  private final HouseholdGoalDataService goalDataService;

  public CustomHouseholdGoalMapper(HouseholdGoalDataService goalDataService) {
    this.goalDataService = goalDataService;
  }

  @Override
  public HouseholdGoal toEntity(AddHouseholdGoalDto dto) {
    return HouseholdGoal.builder()
        .name(dto.getName())
        .description(dto.getDescription())
        .goalTemplateId(dto.getGoalTemplateId())
        .householdId(dto.getHouseholdId())
        .build();
  }

  @Override
  public HouseholdGoalDto toDto(HouseholdGoal entity) {
    List<ProtocolTemplate> protocolTemplates = goalDataService.getProtocolTemplatesForGoalTemplate(entity.getGoalTemplateId());
    return HouseholdGoalDto.builder()
        .id(entity.getId())
        .name(entity.getName())
        .description(entity.getDescription())
        .protocolTemplates(protocolTemplates)
        .build();
  }

  @Override
  public List<HouseholdGoalDto> toDtoList(List<HouseholdGoal> householdGoals) {
    return householdGoals.stream().map(this::toDto).collect(Collectors.toList());
  }
}

