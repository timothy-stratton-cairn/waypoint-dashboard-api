package com.cairn.waypoint.dashboard.endpoints.protocol.mapper;

import com.cairn.waypoint.dashboard.endpoints.protocol.dto.AccountProtocolDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolDto;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolStep;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.StepTemplate;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProtocolMapper {

  ProtocolMapper INSTANCE = Mappers.getMapper(ProtocolMapper.class);

  ProtocolDto toDto(Protocol protocol);

  AccountProtocolDto toAccountProtocolDto(Protocol protocol);

  Protocol protocolTemplateToProtocol(ProtocolTemplate protocolTemplate);

  ProtocolStep protocolStepTemplateToProtocolStep(StepTemplate stepTemplate);
}
