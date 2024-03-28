package com.cairn.waypoint.dashboard.service;

import com.cairn.waypoint.dashboard.entity.QuestionType;
import com.cairn.waypoint.dashboard.entity.enumeration.QuestionTypeEnum;
import com.cairn.waypoint.dashboard.repository.QuestionTypeRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class QuestionTypeService {

  public static final List<QuestionType> availableTypes = new ArrayList<>();

  @SuppressWarnings({"unused", "UnusedAssignment"})
  public QuestionTypeService(QuestionTypeRepository questionTypeRepository) {
    QuestionTypeService.availableTypes.addAll(questionTypeRepository.findAll());

    //Initialize the Enum
    QuestionTypeEnum unused = QuestionTypeEnum.STRING;
    unused = QuestionTypeEnum.INTEGER;
    unused = QuestionTypeEnum.FLOAT;
    unused = QuestionTypeEnum.MULTI_SELECT_OPTION;
    unused = QuestionTypeEnum.SELECT_OPTION;
    unused = QuestionTypeEnum.DATE;
    unused = QuestionTypeEnum.DATETIME;
    unused = QuestionTypeEnum.BOOLEAN;
    unused = QuestionTypeEnum.FILE;
  }
}
