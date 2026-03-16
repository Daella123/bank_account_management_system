package org.example;

import org.example.model.*;
import org.example.model.exceptions.*;
import org.example.service.TransactionManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration-style unit tests for {@link TransactionManager}.
 * <p>
 * Each test verifies that deposit, withdraw, and transfer operations
 * correctly update balances AND record transactions in the manager.
 * </p>
 */
@DisplayName("TransactionManager Tests")
class TransactionManagerTest {

    // -------------------------------------------------------------------------
    // Fixtures
    // -------------------------------------------------------------------------

    private TransactionManager transactionManager;
    private SavingsAccount     savingsAccount;
    private CheckingAccount    checkingAccount;

    /**
     * Resets the transaction manager and creates fresh accounts before each test.
     */
    @BeforeEach
    void setUp() {
        transactionManager = new TransactionManager();

        Customer regular = new RegularCustomer("Alice",   25, "111", "City A");
        Customer premium = new PremiumCustomer("Bob",     35, "222", "City B");

        savingsAccount  = new SavingsAccount(regular,  2000.0);
        checkingAccount = new CheckingAccount(premium, 1500.0);
    }

    // -------------------------------------------------------------------------
    // Deposit tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("deposit() — balance increases and transaction is recorded")
    void depositRecordsTransaction() throws InvalidAmountException {
        double before = savingsAccount.getBalance();
        transactionManager.deposit(savingsAccount, 300.0);

        assertEquals(before + 300.0, savingsAccount.getBalance(), 0.001,
                "Balance should increase after deposit");
        assertEquals(1, transactionManager.getTransactionCount(),
                "One transaction should be recorded");
    }

    @Test
    @DisplayName("deposit() — total deposits aggregate correctly")
    void depositTotalUpdates() throws InvalidAmountException {
        transactionManager.deposit(savingsAccount, 100.0);
        transactionManager.deposit(savingsAccount, 200.0);

        assertEquals(300.0,
                transactionManager.calculateTotalDeposits(savingsAccount.getAccountNumber()),
                0.001,
                "Total deposits should be the sum of all individual deposits");
    }

    // -------------------------------------------------------------------------
    // Withdrawal tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("withdraw() — balance decreases and transaction is recorded")
    void withdrawRecordsTransaction() throws Exception {
        transactionManager.withdraw(savingsAccount, 500.0);  // 2000 - 500 = 1500 > min 500 ✓

        assertEquals(1500.0, savingsAccount.getBalance(), 0.001,
                "Balance should decrease by withdrawn amount");
        assertEquals(1, transactionManager.getTransactionCount(),
                "One transaction should be recorded");
    }

    @Test
    @DisplayName("withdraw() — throws InsufficientFundsException when below min balance")
    void withdrawThrowsInsufficientFunds() {
        // Savings min balance = 500, balance = 2000 → withdraw 1600 → 400 < 500 → throw
        assertThrows(InsufficientFundsException.class,
                () -> transactionManager.withdraw(savingsAccount, 1600.0));
    }

    @Test
    @DisplayName("withdraw() — total withdrawals aggregate correctly")
    void withdrawalTotalUpdates() throws Exception {
        transactionManager.withdraw(savingsAccount, 300.0);
        transactionManager.withdraw(savingsAccount, 200.0);

        assertEquals(500.0,
                transactionManager.calculateTotalWithdrawals(savingsAccount.getAccountNumber()),
                0.001,
                "Total withdrawals should be the sum of all individual withdrawals");
    }

    // -------------------------------------------------------------------------
    // Transfer tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("transfer() — both account balances update correctly")
    void transferUpdatesBothAccounts() throws Exception {
        double savingsBefore  = savingsAccount.getBalance();
        double checkingBefore = checkingAccount.getBalance();

        transactionManager.transfer(savingsAccount, checkingAccount, 500.0);

        assertEquals(savingsBefore  - 500.0, savingsAccount.getBalance(),  0.001,
                "Source balance should decrease");
        assertEquals(checkingBefore + 500.0, checkingAccount.getBalance(), 0.001,
                "Destination balance should increase");
    }

    @Test
    @DisplayName("transfer() — records two transactions (debit + credit)")
    void transferRecordsTwoTransactions() throws Exception {
        transactionManager.transfer(savingsAccount, checkingAccount, 400.0);

        assertEquals(2, transactionManager.getTransactionCount(),
                "A transfer must create exactly 2 transaction records");
    }

    @Test
    @DisplayName("transfer() — same-account transfer throws InvalidAccountException")
    void transferSameAccountThrows() {
        assertThrows(InvalidAccountException.class,
                () -> transactionManager.transfer(savingsAccount, savingsAccount, 100.0),
                "Transfer to same account must throw InvalidAccountException");
    }

    @Test
    @DisplayName("transfer() — insufficient funds in source throws InsufficientFundsException")
    void transferInsufficientFundsThrows() {
        // Savings balance = 2000, min = 500. Transfer 1600 → source balance 400 < 500 → throw
        assertThrows(InsufficientFundsException.class,
                () -> transactionManager.transfer(savingsAccount, checkingAccount, 1600.0));
    }

    // -------------------------------------------------------------------------
    // Transaction count
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getTransactionCount() — starts at zero")
    void initialTransactionCountIsZero() {
        assertEquals(0, transactionManager.getTransactionCount(),
                "A new TransactionManager should have 0 transactions");
    }
}
