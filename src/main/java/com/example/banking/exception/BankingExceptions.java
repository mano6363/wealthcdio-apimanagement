package com.example.banking.exception;

public final class BankingExceptions {
    private BankingExceptions() {}

    public static class AccountNotFoundException extends RuntimeException {
        public AccountNotFoundException(String id) { super("Account not found: " + id); }
    }

    public static class DuplicateAccountException extends RuntimeException {
        public DuplicateAccountException(String id) { super("Account already exists: " + id); }
    }

    public static class InvalidAmountException extends RuntimeException {
        public InvalidAmountException() { super("Amount must be greater than zero"); }
    }

    public static class InsufficientFundsException extends RuntimeException {
        public InsufficientFundsException(String id) { super("Insufficient funds for account: " + id); }
    }
}
