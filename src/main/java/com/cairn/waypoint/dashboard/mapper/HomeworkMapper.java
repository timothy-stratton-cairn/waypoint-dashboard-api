package com.cairn.waypoint.dashboard.mapper;


import com.cairn.waypoint.dashboard.entity.ProtocolStep;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HomeworkMapper {

  HomeworkMapper INSTANCE = Mappers.getMapper(HomeworkMapper.class);
/*
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "updated", ignore = true)
  @Mapping(target = "active", ignore = true)
  @Mapping(source = "homeworkTemplate.name", target = "name")
  @Mapping(source = "homeworkTemplate.description", target = "description")
  @Mapping(source = "modifiedBy", target = "modifiedBy")
  @Mapping(source = "homeworkTemplate", target = "homeworkTemplate")
  @Mapping(source = "protocolStep", target = "protocolStepLinkedHomework.homework")
  @Mapping(source = "householdId", target = "assignedHouseholdId")
//  @Mapping(source = "modifiedBy", target = "homeworkQuestions.modifiedBy")
  Homework templateToInstance(HomeworkTemplate homeworkTemplate, String modifiedBy,
      ProtocolStep protocolStep, Long householdId);*/

}
