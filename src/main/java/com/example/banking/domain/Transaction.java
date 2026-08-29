package com.example.banking.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record Transaction(
        String transactionId,
        String accountId,
        TransactionType type,
        BigDecimal amount,
        BigDecimal balanceAfterTransaction,
        String relatedAccountId,
        Instant timestamp) {
}
