package com.example.banking.service;

import com.example.banking.domain.*;
import com.example.banking.exception.BankingExceptions.*;
import com.example.banking.repository.AccountRepository;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BankingServiceTest {
    private BankingService service;

    @BeforeEach
    void setUp() {
        service = new BankingService(new AccountRepository());
    }

    @Test
    void shouldCreateAccountWithZeroBalance() {
        var account = service.createAccount("ACC-1");
        assertEquals("ACC-1", account.getId());
        assertEquals(BigDecimal.ZERO, account.getBalance());
    }

    @Test
    void shouldRejectDuplicateAccount() {
        service.createAccount("ACC-1");
        assertThrows(DuplicateAccountException.class,
                () -> service.createAccount("ACC-1"));
    }

    @Test
    void shouldDepositMoneyAndRecordLedger() {
        service.createAccount("ACC-1");
        Transaction tx = service.deposit("ACC-1", new BigDecimal("100.00"));

        assertEquals(new BigDecimal("100.00"), service.getAccount("ACC-1").getBalance());
        assertEquals(TransactionType.DEPOSIT, tx.type());
        assertEquals(new BigDecimal("100.00"), tx.balanceAfterTransaction());
        assertNotNull(tx.timestamp());
    }

    @Test
    void shouldWithdrawMoney() {
        service.createAccount("ACC-1");
        service.deposit("ACC-1", new BigDecimal("100.00"));
        service.withdraw("ACC-1", new BigDecimal("40.00"));
        assertEquals(new BigDecimal("60.00"), service.getAccount("ACC-1").getBalance());
    }

    @Test
    void shouldAllowWithdrawalEqualToBalance() {
        service.createAccount("ACC-1");
        service.deposit("ACC-1", new BigDecimal("100"));
        service.withdraw("ACC-1", new BigDecimal("100"));
        assertEquals(BigDecimal.ZERO, service.getAccount("ACC-1").getBalance());
    }

    @Test
    void shouldRejectOverdraftWithoutChangingState() {
        service.createAccount("ACC-1");
        service.deposit("ACC-1", new BigDecimal("100"));

        assertThrows(InsufficientFundsException.class,
                () -> service.withdraw("ACC-1", new BigDecimal("101")));

        assertEquals(new BigDecimal("100"), service.getAccount("ACC-1").getBalance());
        assertEquals(1, service.getTransactions("ACC-1").size());
    }

    @Test
    void shouldRejectZeroAndNegativeAmounts() {
        service.createAccount("ACC-1");
        assertThrows(InvalidAmountException.class,
                () -> service.deposit("ACC-1", BigDecimal.ZERO));
        assertThrows(InvalidAmountException.class,
                () -> service.deposit("ACC-1", new BigDecimal("-1")));
    }

    @Test
    void shouldRejectUnknownAccount() {
        assertThrows(AccountNotFoundException.class,
                () -> service.getAccount("UNKNOWN"));
    }

    @Test
    void shouldTransferBetweenAccounts() {
        service.createAccount("ACC-1");
        service.createAccount("ACC-2");
        service.deposit("ACC-1", new BigDecimal("500"));

        service.transfer("ACC-1", "ACC-2", new BigDecimal("200"));

        assertEquals(new BigDecimal("300"), service.getAccount("ACC-1").getBalance());
        assertEquals(new BigDecimal("200"), service.getAccount("ACC-2").getBalance());
    }

    @Test
    void shouldRecordBothSidesOfTransfer() {
        service.createAccount("ACC-1");
        service.createAccount("ACC-2");
        service.deposit("ACC-1", new BigDecimal("500"));

        service.transfer("ACC-1", "ACC-2", new BigDecimal("200"));

        List<Transaction> source = service.getTransactions("ACC-1");
        List<Transaction> destination = service.getTransactions("ACC-2");

        assertEquals(2, source.size());
        assertEquals(1, destination.size());
        assertEquals(TransactionType.TRANSFER_OUT, source.get(1).type());
        assertEquals(TransactionType.TRANSFER_IN, destination.get(0).type());
    }

    @Test
    void shouldRejectSelfTransfer() {
        service.createAccount("ACC-1");
        assertThrows(IllegalArgumentException.class,
                () -> service.transfer("ACC-1", "ACC-1", new BigDecimal("10")));
    }

    @Test
    void shouldReturnImmutableTransactionSnapshot() {
        service.createAccount("ACC-1");
        service.deposit("ACC-1", new BigDecimal("10"));
        assertEquals(1, service.getTransactions("ACC-1").size());
    }
}
