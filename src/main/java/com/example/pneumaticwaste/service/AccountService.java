package com.example.pneumaticwaste.service;

import com.example.pneumaticwaste.model.Account;
import com.example.pneumaticwaste.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
    }

    public Account createAccount(Account account) {
        if (account.getAccountCode() == null || account.getAccountCode().trim().isEmpty()) {
            throw new RuntimeException("Account code is required");
        }
        if (account.getAccountName() == null || account.getAccountName().trim().isEmpty()) {
            throw new RuntimeException("Account name is required");
        }
        if (account.getBalance() == null) {
            account.setBalance(0.0);
        }
        return accountRepository.save(account);
    }

    public Account updateAccount(Long id, Account details) {
        Account account = getAccountById(id);
        account.setAccountCode(details.getAccountCode());
        account.setAccountName(details.getAccountName());
        account.setAccountType(details.getAccountType());
        if (details.getBalance() != null) {
            account.setBalance(details.getBalance());
        }
        return accountRepository.save(account);
    }
}
