package com.cairn.waypoint.dashboard.service;


import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.repository.ProtocolRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ProtocolService {

  private final ProtocolRepository protocolRepository;

  public ProtocolService(ProtocolRepository protocolRepository) {
    this.protocolRepository = protocolRepository;
  }

  public List<Protocol> getAllProtocols() {
    return this.protocolRepository.findAll();
  }

  public Optional<Protocol> getProtocolById(Long id) {
    return this.protocolRepository.findById(id);
  }
}
