package com.cairn.waypoint.dashboard.service.helper;

import com.cairn.waypoint.dashboard.endpoints.homeworkquestion.dto.importHomeworkQuestionDto;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.ExpectedResponse;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.HomeworkCategory;
import com.cairn.waypoint.dashboard.entity.enumeration.ProtocolStatusEnum;
import com.cairn.waypoint.dashboard.entity.enumeration.TemplateStatusEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
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
  //@Mapping(source = "triggeringResponse", target = "triggeringResponse")
  //@Mapping(source = "protocolTemplate", target = "triggeredProtocol")
  @Mapping(target = "expectedHomeworkResponses", ignore = true)
  HomeworkQuestion toEntity(importHomeworkQuestionDto dto);

}
