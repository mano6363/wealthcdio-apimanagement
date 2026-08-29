package com.example.banking.domain;

import java.math.BigDecimal;
import java.util.Objects;

public class Account {
    private final String id;
    private BigDecimal balance;

    public Account(String id) {
        this(id, BigDecimal.ZERO);
    }

    public Account(String id, BigDecimal balance) {
        this.id = Objects.requireNonNull(id);
        this.balance = Objects.requireNonNull(balance);
    }

    public String getId() { return id; }
    public BigDecimal getBalance() { return balance; }

    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        balance = balance.subtract(amount);
    }
}
