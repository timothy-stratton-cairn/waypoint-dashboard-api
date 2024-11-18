package com.cairn.waypoint.dashboard.service.helper;

import com.cairn.waypoint.dashboard.endpoints.homeworkquestion.dto.importHomeworkQuestionDto;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.ExpectedResponse;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.HomeworkCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface QuestionMapper {

  QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

  @Mapping(source = "abbreviation", target = "questionAbbreviation")
  @Mapping(source = "question", target = "question")
  @Mapping(source = "questionType", target = "questionType")
  @Mapping(source = "isRequired", target = "required")
  @Mapping(source = "category", target = "category")
  @Mapping(source = "triggersProtocolCreation", target = "triggersProtocolCreation")
  @Mapping(source = "triggeringResponse", target = "triggeringResponse")
  @Mapping(source = "protocol", target = "triggeredProtocol")
  @Mapping(target = "status", ignore = true) // TODO: Default or map if needed
  @Mapping(target = "active", ignore = true) // TODO: Set a default value if needed
  @Mapping(target = "label", ignore = true) // TODO: Map or implement logic if applicable
  @Mapping(target = "expectedHomeworkResponses", ignore = true) // TODO: Parse expectedResponse into Set<ExpectedResponse>
  HomeworkQuestion toEntity(importHomeworkQuestionDto dto);

}

