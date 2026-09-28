package com.example.pneumaticwaste.repository;

import com.example.pneumaticwaste.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountCode(String accountCode);
    Optional<Account> findFirstByAccountType(String accountType);
}
