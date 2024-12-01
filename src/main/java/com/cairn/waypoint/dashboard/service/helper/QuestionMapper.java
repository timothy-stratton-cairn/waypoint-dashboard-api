package com.cairn.waypoint.dashboard.service.helper;

import com.cairn.waypoint.dashboard.endpoints.homeworkquestion.dto.importHomeworkQuestionDto;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
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
  @Mapping(target = "expectedHomeworkResponses", ignore = true)
  @Mapping(target = "active", constant = "true")
  @Mapping(target = "status", constant = "LIVE")
  HomeworkQuestion toEntity(importHomeworkQuestionDto dto);

}
