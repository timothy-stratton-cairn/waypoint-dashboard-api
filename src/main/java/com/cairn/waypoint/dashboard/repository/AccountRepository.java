package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.dto.AccountDetailsDto;
import com.cairn.waypoint.dashboard.dto.AccountListDto;
import com.cairn.waypoint.dashboard.dto.BatchAddAccountDetailsListDto;
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
public class AccountRepository {

  private final WebClient webClient;

  public AccountRepository(@Qualifier("authorizationApiWebClient") WebClient webclient) {
    this.webClient = webclient;
  }

  @SuppressWarnings("deprecation")
  public Optional<AccountDetailsDto> getAccountById(Long accountId) {
    final String PATH = "/api/account/{accountId}";

    return webClient.get()
        .uri(PATH, accountId)
        .header("Authorization",
            "Bearer " + ((Jwt) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).getTokenValue())
        .retrieve()
        .bodyToMono(AccountDetailsDto.class)
        .onErrorResume(WebClientResponseException.class,
            ex -> ex.getRawStatusCode() == 404 ? Mono.empty() : Mono.error(ex))
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

  public ClientCreationResponseListDto batchAddAccounts(
      BatchAddAccountDetailsListDto accountsToAdd) {
    final String PATH = "/api/account/batch";

    return webClient.post()
        .uri(uriBuilder -> uriBuilder
            .path(PATH)
            .build())
        .body(BodyInserters.fromValue(accountsToAdd))
        .header("Authorization",
            "Bearer " + ((Jwt) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).getTokenValue())
        .retrieve()
        .bodyToMono(ClientCreationResponseListDto.class)
        .block();
  }
}
