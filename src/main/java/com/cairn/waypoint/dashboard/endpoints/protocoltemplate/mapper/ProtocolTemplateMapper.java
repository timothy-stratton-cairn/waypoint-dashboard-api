package com.cairn.waypoint.dashboard.endpoints.protocoltemplate.mapper;

import com.cairn.waypoint.dashboard.endpoints.protocoltemplate.dto.AddProtocolTemplateDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.protocoltemplate.dto.ProtocolTemplateDto;
import com.cairn.waypoint.dashboard.endpoints.protocoltemplate.dto.UpdateProtocolTemplateDetailsDto;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProtocolTemplateMapper {

  ProtocolTemplateMapper INSTANCE = Mappers.getMapper(ProtocolTemplateMapper.class);

  ProtocolTemplateDto toDto(ProtocolTemplate protocolTemplate);

  @Mapping(target = "protocolTemplateSteps", ignore = true)
  ProtocolTemplate toEntity(AddProtocolTemplateDetailsDto addProtocolTemplateDetailsDto);

  @Mapping(target = "protocolTemplateSteps", ignore = true)
  ProtocolTemplate toEntity(
      UpdateProtocolTemplateDetailsDto updateProtocolTemplateDetailsDto);
}
