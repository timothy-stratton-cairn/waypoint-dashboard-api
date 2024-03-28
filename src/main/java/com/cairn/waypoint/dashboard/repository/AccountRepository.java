package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.dto.AccountDetailsDto;
import com.cairn.waypoint.dashboard.dto.AccountListDto;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

@Repository
public class AccountRepository {

  private final WebClient webClient;

  public AccountRepository(@Qualifier("authorizationApiWebClient") WebClient webclient) {
    this.webClient = webclient;
  }

  public Optional<AccountDetailsDto> getAccountById(Long accountId) {
    final String PATH = "/api/account/{accountId}";

    return webClient.get()
        .uri(PATH, accountId)
        .header("Authorization",
            "Bearer " + ((Jwt) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).getTokenValue())
        .retrieve()
        .bodyToMono(AccountDetailsDto.class)
        .blockOptional();
  }

  public AccountListDto getAccountsById(List<Long> accountIds) {
    final String PATH = "/api/account";

    return webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(PATH)
            .queryParam("accountId", accountIds)
            .build())
        .header("Authorization",
            "Bearer " + ((Jwt) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).getTokenValue())
        .retrieve()
        .bodyToMono(AccountListDto.class)
        .block();
  }
}
