package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.dto.authorization.HouseholdDetailsDto;
import com.cairn.waypoint.dashboard.dto.authorization.HouseholdListDto;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.repository.HouseholdRepository;
import com.cairn.waypoint.dashboard.repository.ProtocolRepository;
import com.cairn.waypoint.dashboard.repository.ProtocolTemplateRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class HouseholdDataService {

  private final HouseholdRepository householdRepository;
  private final ProtocolRepository protocolRepository;
  public HouseholdDataService(HouseholdRepository HouseholdRepository, ProtocolRepository protocolRepository) {
    this.householdRepository = HouseholdRepository;
    this.protocolRepository = protocolRepository;
  }

  public Optional<HouseholdDetailsDto> getHouseholdDetails(Long householdId) {
    return this.householdRepository.getHouseholdById(householdId);
  }

  public HouseholdListDto getHouseholdDetailsListByIdList(List<Long> householdIds) {
    return this.householdRepository.getHouseholdsListById(householdIds);
  }
}
