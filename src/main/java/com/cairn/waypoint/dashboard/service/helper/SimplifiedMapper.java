package com.cairn.waypoint.dashboard.service.helper;

import com.cairn.waypoint.dashboard.endpoints.homeworkquestion.SimplifiedHomeworkQuestionDto;
import com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto.SimplifiedHomeworkResponseDto;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.HomeworkResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface SimplifiedMapper {

  @Mapping(source = "category.id", target = "categoryId")
  SimplifiedHomeworkQuestionDto simplifyQuestion(HomeworkQuestion question);

  SimplifiedHomeworkResponseDto simplifyResponse(HomeworkResponse response);



}


