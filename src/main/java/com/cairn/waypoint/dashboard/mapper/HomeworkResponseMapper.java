package com.cairn.waypoint.dashboard.mapper;

import com.cairn.waypoint.dashboard.entity.HomeworkQuestionLinkedProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.HomeworkResponse;
import java.util.HashSet;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HomeworkResponseMapper {

  HomeworkResponseMapper INSTANCE = Mappers.getMapper(HomeworkResponseMapper.class);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "updated", ignore = true)
  @Mapping(target = "active", ignore = true)
  @Mapping(source = "modifiedBy", target = "modifiedBy")
  
  HomeworkResponse homeworkQuestionLinkedProtocolTemplate(HomeworkQuestionLinkedProtocolTemplate protocolTemplate,String modifiedBy);
}
