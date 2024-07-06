package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.dto.authorization.RoleListDto;
import com.cairn.waypoint.dashboard.repository.RoleRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RoleDataService {

  private final RoleRepository roleRepository;

  public RoleDataService(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  public RoleListDto getRolesById(List<Long> roleIds) {
    return this.roleRepository.getRolesById(roleIds);
  }
}
