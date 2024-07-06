package com.cairn.waypoint.dashboard.utility.protocoltriggering;

import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.enumeration.RecurrenceTypeEnum;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatusManager {

  public static final Map<RecurrenceTypeEnum, List<StatusObserver>> observers = new HashMap<>();

  static {
    for (RecurrenceTypeEnum subscribedOperation : RecurrenceTypeEnum.values()) {
      StatusManager.observers.put(subscribedOperation, new ArrayList<>());
    }
  }

  public static void subscribe(RecurrenceTypeEnum subscriptionType, StatusObserver observer) {
    List<StatusObserver> statusObservers = StatusManager.observers.get(subscriptionType);
    statusObservers.add(observer);
  }

  public static void unsubscribe(RecurrenceTypeEnum subscriptionType, StatusObserver observer) {
    List<StatusObserver> statusObservers = StatusManager.observers.get(subscriptionType);
    statusObservers.remove(observer);
  }

  public static void notify(Protocol updatedProtocol) {
    StatusManager.observers.get(updatedProtocol.getRecurrenceType())
        .forEach(observer -> observer.update(updatedProtocol));
  }
}
