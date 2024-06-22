package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.dto.authorization.AccountDetailsDto;
import com.cairn.waypoint.dashboard.dto.authorization.AccountListDto;
import com.cairn.waypoint.dashboard.dto.authorization.BatchAddAccountDetailsListDto;
import com.cairn.waypoint.dashboard.endpoints.ops.dto.ClientCreationResponseListDto;
import com.cairn.waypoint.dashboard.repository.AccountRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AccountDataService {

  private final AccountRepository accountRepository;

  public AccountDataService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  public Optional<AccountDetailsDto> getAccountDetails(String accountIdOrUsername) {
    return this.accountRepository.getAccountByIdOrUsername(accountIdOrUsername);
  }

  public AccountListDto getAccountsById(List<Long> accountIds) {
    return this.accountRepository.getAccountsById(accountIds);
  }

  public ClientCreationResponseListDto createBatchAccounts(
      BatchAddAccountDetailsListDto accountsToAdd) {
    return this.accountRepository.batchAddAccounts(accountsToAdd);
  }
}
