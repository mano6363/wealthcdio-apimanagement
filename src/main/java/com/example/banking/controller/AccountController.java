package com.example.banking.controller;

import com.example.banking.domain.Transaction;
import com.example.banking.dto.Requests.*;
import com.example.banking.dto.Responses.*;
import com.example.banking.service.BankingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AccountController {
    private final BankingService bankingService;

    public AccountController(BankingService bankingService) {
        this.bankingService = bankingService;
    }

    @PostMapping("/accounts")
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest request) {
        return AccountResponse.from(bankingService.createAccount(request.accountId()));
    }

    @GetMapping("/accounts/{id}")
    public AccountResponse get(@PathVariable String id) {
        return AccountResponse.from(bankingService.getAccount(id));
    }

    @PostMapping("/accounts/{id}/deposit")
    public TransactionResponse deposit(@PathVariable String id,
                                       @Valid @RequestBody AmountRequest request) {
        return TransactionResponse.from(bankingService.deposit(id, request.amount()));
    }

    @PostMapping("/accounts/{id}/withdraw")
    public TransactionResponse withdraw(@PathVariable String id,
                                        @Valid @RequestBody AmountRequest request) {
        return TransactionResponse.from(bankingService.withdraw(id, request.amount()));
    }

    @PostMapping("/transfers")
    public ResponseEntity<Void> transfer(@Valid @RequestBody TransferRequest request) {
        bankingService.transfer(request.fromAccountId(), request.toAccountId(), request.amount());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/accounts/{id}/transactions")
    public List<TransactionResponse> transactions(@PathVariable String id) {
        return bankingService.getTransactions(id).stream()
                .map(TransactionResponse::from).toList();
    }
}
