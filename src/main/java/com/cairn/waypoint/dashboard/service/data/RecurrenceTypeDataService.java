package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.entity.RecurrenceType;
import com.cairn.waypoint.dashboard.entity.enumeration.RecurrenceTypeEnum;
import com.cairn.waypoint.dashboard.repository.RecurrenceTypeRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RecurrenceTypeDataService {

  public static final List<RecurrenceType> availableStatuses = new ArrayList<>();

  @SuppressWarnings({"unused", "UnusedAssignment"})
  public RecurrenceTypeDataService(RecurrenceTypeRepository recurrenceTypeRepository) {
    RecurrenceTypeDataService.availableStatuses.addAll(recurrenceTypeRepository.findAll());

    //Initialize the Enum
    RecurrenceTypeEnum unused = RecurrenceTypeEnum.ON_STATUS;
    unused = RecurrenceTypeEnum.ON_SCHEDULE;
    unused = RecurrenceTypeEnum.MANUAL;
  }
}
