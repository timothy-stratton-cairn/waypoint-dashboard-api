package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.entity.ProtocolCommentType;
import com.cairn.waypoint.dashboard.entity.enumeration.ProtocolCommentTypeEnum;
import com.cairn.waypoint.dashboard.repository.ProtocolCommentTypeRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProtocolCommentTypeDataService {

  public static final List<ProtocolCommentType> availableCommentTypes = new ArrayList<>();

  @SuppressWarnings({"unused", "UnusedAssignment"})
  public ProtocolCommentTypeDataService(ProtocolCommentTypeRepository protocolCommentTypeRepository) {
    ProtocolCommentTypeDataService.availableCommentTypes.addAll(protocolCommentTypeRepository.findAll());

    //Initialize the Enum
    ProtocolCommentTypeEnum unused = ProtocolCommentTypeEnum.COMMENT;
    unused = ProtocolCommentTypeEnum.RECOMMENDATION;
    unused = ProtocolCommentTypeEnum.INTERNAL_MEETING_NOTE;
  }
}
