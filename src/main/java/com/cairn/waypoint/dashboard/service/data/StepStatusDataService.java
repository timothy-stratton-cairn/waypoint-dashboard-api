package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.entity.StepStatus;
import com.cairn.waypoint.dashboard.entity.enumeration.StepStatusEnum;
import com.cairn.waypoint.dashboard.repository.StepStatusRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StepStatusDataService {

  public static final List<StepStatus> availableStatuses = new ArrayList<>();

  @SuppressWarnings({"unused", "UnusedAssignment"})
  public StepStatusDataService(StepStatusRepository stepStatusRepository) {
    StepStatusDataService.availableStatuses.addAll(stepStatusRepository.findAll());

    //Initialize the Enum
    StepStatusEnum unused = StepStatusEnum.TODO;
    unused = StepStatusEnum.IN_PROGRESS;
    unused = StepStatusEnum.DONE;
  }
}
