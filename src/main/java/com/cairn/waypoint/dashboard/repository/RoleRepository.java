package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.dto.authorization.RoleListDto;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

@Repository
public class RoleRepository {

  private final WebClient webClient;

  public RoleRepository(@Qualifier("authorizationApiWebClient") WebClient webclient) {
    this.webClient = webclient;
  }

  public RoleListDto getRolesById(List<Long> roleIds) {
    final String PATH = "/api/role";

    return webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(PATH)
            .queryParam("roleId", roleIds)
            .build())
        .header("Authorization",
            "Bearer " + ((Jwt) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).getTokenValue())
        .retrieve()
        .bodyToMono(RoleListDto.class)
        .block();
  }
}
