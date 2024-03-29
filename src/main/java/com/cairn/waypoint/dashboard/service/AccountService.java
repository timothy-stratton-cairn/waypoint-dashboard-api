package com.cairn.waypoint.dashboard.service;

import com.cairn.waypoint.dashboard.dto.AccountDetailsDto;
import com.cairn.waypoint.dashboard.dto.AccountListDto;
import com.cairn.waypoint.dashboard.repository.AccountRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

  private final AccountRepository accountRepository;

  public AccountService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  public Optional<AccountDetailsDto> getAccountDetails(Long accountId) {
    return this.accountRepository.getAccountById(accountId);
  }

  public AccountListDto getAccountsById(List<Long> accountIds) {
    return this.accountRepository.getAccountsById(accountIds);
  }
}
