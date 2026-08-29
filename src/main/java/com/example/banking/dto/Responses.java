package com.example.banking.dto;

import com.example.banking.domain.Account;
import com.example.banking.domain.Transaction;
import com.example.banking.domain.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;

public final class Responses {
    private Responses() {}

    public record AccountResponse(String id, BigDecimal balance) {
        public static AccountResponse from(Account a) {
            return new AccountResponse(a.getId(), a.getBalance());
        }
    }

    public record TransactionResponse(
            String transactionId, String accountId, TransactionType type,
            BigDecimal amount, BigDecimal balanceAfterTransaction,
            String relatedAccountId, Instant timestamp) {
        public static TransactionResponse from(Transaction t) {
            return new TransactionResponse(t.transactionId(), t.accountId(), t.type(),
                    t.amount(), t.balanceAfterTransaction(), t.relatedAccountId(), t.timestamp());
        }
    }

    public record ErrorResponse(Instant timestamp, int status, String error, String message) {
        public static ErrorResponse of(int status, String error, String message) {
            return new ErrorResponse(Instant.now(), status, error, message);
        }
    }
}
