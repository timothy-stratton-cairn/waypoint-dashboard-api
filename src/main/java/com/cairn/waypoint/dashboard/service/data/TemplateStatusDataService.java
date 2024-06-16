package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.entity.TemplateStatus;
import com.cairn.waypoint.dashboard.entity.enumeration.TemplateStatusEnum;
import com.cairn.waypoint.dashboard.repository.TemplateStatusRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TemplateStatusDataService {

  public static final List<TemplateStatus> availableStatuses = new ArrayList<>();

  @SuppressWarnings({"unused", "UnusedAssignment"})
  public TemplateStatusDataService(TemplateStatusRepository templateStatusRepository) {
    TemplateStatusDataService.availableStatuses.addAll(templateStatusRepository.findAll());

    //Initialize the Enum
    TemplateStatusEnum unused = TemplateStatusEnum.INACTIVE;
    unused = TemplateStatusEnum.LIVE;
    unused = TemplateStatusEnum.ARCHIVED;
  }
}
