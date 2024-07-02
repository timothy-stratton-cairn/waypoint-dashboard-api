package com.cairn.waypoint.dashboard.mapper;

import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolCommentDto;
import com.cairn.waypoint.dashboard.entity.ProtocolCommentary;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProtocolCommentMapper {

  ProtocolCommentMapper INSTANCE = Mappers.getMapper(ProtocolCommentMapper.class);

  @Mapping(target = "commentId", source = "id")
  @Mapping(target = "takenAt", source = "created")
  @Mapping(target = "takenBy", source = "originalCommenter")
  ProtocolCommentDto toDto(ProtocolCommentary protocolCommentary);

  List<ProtocolCommentDto> protocolCommentListToDtoList(Set<ProtocolCommentary> protocolComments);
}
