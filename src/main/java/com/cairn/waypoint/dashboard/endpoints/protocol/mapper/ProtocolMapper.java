package com.cairn.waypoint.dashboard.endpoints.protocol.mapper;

import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolDto;
import com.cairn.waypoint.dashboard.entity.Protocol;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProtocolMapper {

  ProtocolMapper INSTANCE = Mappers.getMapper(ProtocolMapper.class);

  ProtocolDto toDto(Protocol protocol);
}
