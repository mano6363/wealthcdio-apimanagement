package com.example.banking.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public final class Requests {
    private Requests() {}

    public record CreateAccountRequest(
            @NotBlank(message = "accountId must not be blank") String accountId) {}

    public record AmountRequest(
            @NotNull(message = "amount is required")
            @DecimalMin(value = "0.01", message = "amount must be greater than zero")
            BigDecimal amount) {}

    public record TransferRequest(
            @NotBlank(message = "fromAccountId must not be blank") String fromAccountId,
            @NotBlank(message = "toAccountId must not be blank") String toAccountId,
            @NotNull(message = "amount is required")
            @DecimalMin(value = "0.01", message = "amount must be greater than zero")
            BigDecimal amount) {}
}
