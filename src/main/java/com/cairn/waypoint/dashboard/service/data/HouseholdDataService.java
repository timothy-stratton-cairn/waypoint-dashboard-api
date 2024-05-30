package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.dto.HouseholdDetailsDto;
import com.cairn.waypoint.dashboard.repository.HouseholdRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class HouseholdDataService {

  private final HouseholdRepository HouseholdRepository;

  public HouseholdDataService(HouseholdRepository HouseholdRepository) {
    this.HouseholdRepository = HouseholdRepository;
  }

  public Optional<HouseholdDetailsDto> getHouseholdDetails(Long HouseholdId) {
    return this.HouseholdRepository.getHouseholdById(HouseholdId);
  }
}
