package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.entity.ProtocolUser;
import com.cairn.waypoint.dashboard.repository.ProtocolUserRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProtocolUserDataService {

  private final ProtocolUserRepository protocolUserRepository;

  public ProtocolUserDataService(ProtocolUserRepository protocolUserRepository) {
    this.protocolUserRepository = protocolUserRepository;
  }

  public ProtocolUser saveProtocolUser(ProtocolUser protocolUser) {
    return protocolUserRepository.save(protocolUser);
  }

  public List<ProtocolUser> getAllProtocolsForAccountId(Long userId) {
    return protocolUserRepository.findByUserId(userId);
  }

}
