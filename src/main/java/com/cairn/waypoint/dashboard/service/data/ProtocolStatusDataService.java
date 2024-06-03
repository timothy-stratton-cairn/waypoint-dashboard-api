package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.entity.ProtocolStatus;
import com.cairn.waypoint.dashboard.entity.enumeration.ProtocolStatusEnum;
import com.cairn.waypoint.dashboard.repository.ProtocolStatusRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProtocolStatusDataService {

  public static final List<ProtocolStatus> availableStatuses = new ArrayList<>();

  @SuppressWarnings({"unused", "UnusedAssignment"})
  public ProtocolStatusDataService(ProtocolStatusRepository protocolStatusRepository) {
    ProtocolStatusDataService.availableStatuses.addAll(protocolStatusRepository.findAll());

    //Initialize the Enum
    ProtocolStatusEnum unused = ProtocolStatusEnum.IN_PROGRESS;
    unused = ProtocolStatusEnum.COMPLETED;
    unused = ProtocolStatusEnum.ARCHIVED;
  }
}
