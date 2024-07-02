package com.cairn.waypoint.dashboard.utility.protocoltriggering;

import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.enumeration.ProtocolStatusEnum;
import com.cairn.waypoint.dashboard.entity.enumeration.RecurrenceTypeEnum;
import java.time.LocalDate;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

@Component
public class OnCompletionStatusObserver implements StatusObserver {
  private final ProtocolStatusEnum interestedStatus = ProtocolStatusEnum.COMPLETED;

  @Override
  public void update(ProtocolStatusEnum statusOperation, Protocol protocol) {
    if (protocol.getRecurrenceType() == RecurrenceTypeEnum.ON_COMPLETION) {
      this.createReoccurrence(protocol);
    }
  }

  @Override
  public void createReoccurrence(Protocol protocol) {
    Protocol protocolRecurrence = SerializationUtils.clone(protocol);

    protocolRecurrence.setModifiedBy("triggering-system");
    protocolRecurrence.setDueDate(LocalDate.now()
        .plusYears(protocol.getReoccurInYears())
        .plusMonths(protocol.getReoccurInMonths())
        .plusDays(protocol.getReoccurInDays()));
  }


}
