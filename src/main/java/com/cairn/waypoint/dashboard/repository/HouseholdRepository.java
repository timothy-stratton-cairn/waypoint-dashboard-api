package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.dto.AccountDetailsDto;
import com.cairn.waypoint.dashboard.dto.AccountListDto;
import com.cairn.waypoint.dashboard.dto.BatchAddAccountDetailsListDto;
import com.cairn.waypoint.dashboard.dto.HouseholdDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.ops.dto.ClientCreationResponseListDto;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Repository
public class HouseholdRepository {

  private final WebClient webClient;

  public HouseholdRepository(@Qualifier("authorizationApiWebClient") WebClient webclient) {
    this.webClient = webclient;
  }

  @SuppressWarnings("deprecation")
  public Optional<HouseholdDetailsDto> getHouseholdById(Long householdId) {
    final String PATH = "/api/household/{householdId}";

    return webClient.get()
        .uri(PATH, householdId)
        .header("Authorization",
            "Bearer " + ((Jwt) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).getTokenValue())
        .retrieve()
        .bodyToMono(HouseholdDetailsDto.class)
        .onErrorResume(WebClientResponseException.class,
            ex -> ex.getRawStatusCode() == 404 ? Mono.empty() : Mono.error(ex))
        .blockOptional();
  }
}