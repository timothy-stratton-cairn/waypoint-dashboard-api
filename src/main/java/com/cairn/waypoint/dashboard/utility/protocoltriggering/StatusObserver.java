package com.cairn.waypoint.dashboard.utility.protocoltriggering;

import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.enumeration.ProtocolStatusEnum;

public interface StatusObserver {
  void update(ProtocolStatusEnum statusOperation, Protocol protocol);

  void createReoccurrence(Protocol protocol);
}
