package com.example.banking.service;

import com.example.banking.domain.Account;
import com.example.banking.domain.Transaction;
import com.example.banking.domain.TransactionType;
import com.example.banking.exception.BankingExceptions.*;
import com.example.banking.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class BankingService {
    private final AccountRepository accountRepository;
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<Transaction>> ledger =
            new ConcurrentHashMap<>();

    public BankingService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public synchronized Account createAccount(String accountId) {
        if (accountId == null || accountId.isBlank()) {
            throw new IllegalArgumentException("accountId must not be blank");
        }
        if (accountRepository.existsById(accountId)) {
            throw new DuplicateAccountException(accountId);
        }
        Account account = new Account(accountId);
        accountRepository.save(account);
        ledger.put(accountId, new CopyOnWriteArrayList<>());
        return account;
    }

    public Account getAccount(String accountId) {
        return existing(accountId);
    }

    public synchronized Transaction deposit(String accountId, BigDecimal amount) {
        validateAmount(amount);
        Account account = existing(accountId);
        account.deposit(amount);
        return record(account, TransactionType.DEPOSIT, amount, null);
    }

    public synchronized Transaction withdraw(String accountId, BigDecimal amount) {
        validateAmount(amount);
        Account account = existing(accountId);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(accountId);
        }
        account.withdraw(amount);
        return record(account, TransactionType.WITHDRAWAL, amount, null);
    }

    public synchronized void transfer(String fromId, String toId, BigDecimal amount) {
        validateAmount(amount);
        if (fromId == null || toId == null || fromId.equals(toId)) {
            throw new IllegalArgumentException("Source and destination accounts must be different");
        }

        Account source = existing(fromId);
        Account destination = existing(toId);

        if (source.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(fromId);
        }

        source.withdraw(amount);
        destination.deposit(amount);

        record(source, TransactionType.TRANSFER_OUT, amount, destination.getId());
        record(destination, TransactionType.TRANSFER_IN, amount, source.getId());
    }

    public List<Transaction> getTransactions(String accountId) {
        existing(accountId);
        return List.copyOf(ledger.get(accountId));
    }

    private Account existing(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("accountId must not be blank");
        }
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException();
        }
    }

    private Transaction record(Account account, TransactionType type,
                               BigDecimal amount, String relatedAccountId) {
        Transaction transaction = new Transaction(
                UUID.randomUUID().toString(), account.getId(), type, amount,
                account.getBalance(), relatedAccountId, Instant.now());
        ledger.get(account.getId()).add(transaction);
        return transaction;
    }
}
