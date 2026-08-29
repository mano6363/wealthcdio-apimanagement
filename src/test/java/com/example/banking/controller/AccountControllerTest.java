package com.example.banking.controller;

import com.example.banking.repository.AccountRepository;
import com.example.banking.service.BankingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AccountControllerTest {
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(new AccountController(
                        new BankingService(new AccountRepository())))
                .setControllerAdvice(new com.example.banking.exception.GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldCreateAndGetAccount() throws Exception {
        mvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"accountId":"ACC-100"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("ACC-100"))
                .andExpect(jsonPath("$.balance").value(0));

        mvc.perform(get("/api/accounts/ACC-100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(0));
    }

    @Test
    void shouldDeposit() throws Exception {
        create("ACC-100");

        mvc.perform(post("/api/accounts/ACC-100/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"amount":100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("DEPOSIT"))
                .andExpect(jsonPath("$.amount").value(100))
                .andExpect(jsonPath("$.balanceAfterTransaction").value(100));
    }

    @Test
    void shouldRejectInvalidAmount() throws Exception {
        create("ACC-100");

        mvc.perform(post("/api/accounts/ACC-100/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"amount":0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldTransfer() throws Exception {
        create("ACC-1");
        create("ACC-2");

        mvc.perform(post("/api/accounts/ACC-1/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"amount":500}"))
                .andExpect(status().isOk());

        mvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"fromAccountId":"ACC-1","toAccountId":"ACC-2","amount":200}"))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/accounts/ACC-1"))
                .andExpect(jsonPath("$.balance").value(300));
        mvc.perform(get("/api/accounts/ACC-2"))
                .andExpect(jsonPath("$.balance").value(200));
    }

    @Test
    void shouldReturnHistory() throws Exception {
        create("ACC-1");
        mvc.perform(post("/api/accounts/ACC-1/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"amount":100}"))
                .andExpect(status().isOk());

        mvc.perform(get("/api/accounts/ACC-1/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].type").value("DEPOSIT"));
    }

    private void create(String id) throws Exception {
        mvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"accountId":"" + id + ""}"))
                .andExpect(status().isOk());
    }
}
