package com.cairn.waypoint.dashboard.utility.protocoltriggering;

import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.enumeration.ProtocolStatusEnum;
import com.cairn.waypoint.dashboard.entity.enumeration.RecurrenceTypeEnum;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class StatusManager {
  Map<ProtocolStatusEnum, List<StatusObserver>> observers = new HashMap<>();

  public StatusManager() {
    for (ProtocolStatusEnum statusOperation : ProtocolStatusEnum.values()) {
      this.observers.put(statusOperation, new ArrayList<>());
    }
  }

  public void subscribe(ProtocolStatusEnum statusOperationEvent, StatusObserver observer) {
    List<StatusObserver> statusObservers = this.observers.get(statusOperationEvent);
    statusObservers.add(observer);
  }

  public void unsubscribe(ProtocolStatusEnum statusOperationEvent, StatusObserver observer) {
    List<StatusObserver> statusObservers = this.observers.get(statusOperationEvent);
    statusObservers.remove(observer);
  }

  public void notify(ProtocolStatusEnum statusOperationEvent, Protocol updatedProtocol) {
    List<StatusObserver> statusObservers = this.observers.get(statusOperationEvent);
    for (StatusObserver observer : statusObservers) {
      observer.update(statusOperationEvent, updatedProtocol);
    }
  }
}
