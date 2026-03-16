package org.example;

import org.example.model.Customer;
import org.example.model.Account;
import org.example.model.SavingsAccount;
import org.example.model.CheckingAccount;
import org.example.model.RegularCustomer;
import org.example.model.PremiumCustomer;
import org.example.model.exceptions.InvalidAmountException;
import org.example.model.exceptions.InsufficientFundsException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Account}, {@link SavingsAccount}, and {@link CheckingAccount}.
 * <p>
 * Uses {@link BeforeEach} to reset fresh account instances before each test,
 * ensuring test isolation.
 * </p>
 */
@DisplayName("Account Tests")
class AccountTest {

    // -------------------------------------------------------------------------
    // Test fixtures
    // -------------------------------------------------------------------------

    private SavingsAccount  savingsAccount;
    private CheckingAccount checkingAccount;

    /**
     * Creates fresh account instances before every test to guarantee isolation.
     */
    @BeforeEach
    void setUp() {
        Customer regularCustomer = new RegularCustomer("Test User", 30, "123-456", "Test City");
        Customer premiumCustomer = new PremiumCustomer("Premium User", 40, "789-012", "Premium City");

        savingsAccount  = new SavingsAccount(regularCustomer, 1000.0);
        checkingAccount = new CheckingAccount(premiumCustomer, 500.0);
    }

    // -------------------------------------------------------------------------
    // Deposit tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("deposit() — valid amount increases balance correctly")
    void depositUpdatesBalance() throws InvalidAmountException {
        double balanceBefore = savingsAccount.getBalance();
        savingsAccount.deposit(500.0);
        assertEquals(balanceBefore + 500.0, savingsAccount.getBalance(), 0.001,
                "Balance should increase by the deposited amount");
    }

    @Test
    @DisplayName("deposit() — zero amount throws InvalidAmountException")
    void depositZeroThrowsInvalidAmountException() {
        assertThrows(InvalidAmountException.class,
                () -> savingsAccount.deposit(0),
                "Depositing 0 must throw InvalidAmountException");
    }

    @Test
    @DisplayName("deposit() — negative amount throws InvalidAmountException")
    void depositNegativeThrowsInvalidAmountException() {
        assertThrows(InvalidAmountException.class,
                () -> savingsAccount.deposit(-200.0),
                "Depositing a negative amount must throw InvalidAmountException");
    }

    // -------------------------------------------------------------------------
    // Savings withdraw tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("SavingsAccount.withdraw() — valid amount decreases balance correctly")
    void withdrawUpdatesBalance() throws Exception {
        double balanceBefore = savingsAccount.getBalance();
        savingsAccount.withdraw(400.0);  // balance → 600 (above min 500)
        assertEquals(balanceBefore - 400.0, savingsAccount.getBalance(), 0.001,
                "Balance should decrease by the withdrawn amount");
    }

    @Test
    @DisplayName("SavingsAccount.withdraw() — below minimum balance throws InsufficientFundsException")
    void withdrawBelowMinimumThrowsException() {
        // Savings min balance = 500, current = 1000. Withdraw 600 → 400 < 500 → should throw
        assertThrows(InsufficientFundsException.class,
                () -> savingsAccount.withdraw(600.0),
                "Withdraw that would breach minimum balance must throw InsufficientFundsException");
    }

    @Test
    @DisplayName("SavingsAccount.withdraw() — exact amount to minimum balance is allowed")
    void withdrawToMinimumBalanceAllowed() throws Exception {
        // Balance 1000, min 500 → withdraw 500 exactly → balance = 500 → allowed
        savingsAccount.withdraw(500.0);
        assertEquals(500.0, savingsAccount.getBalance(), 0.001,
                "Withdrawing to exactly the minimum balance must be allowed");
    }

    // -------------------------------------------------------------------------
    // Checking account (overdraft) tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("CheckingAccount.withdraw() — within overdraft limit is allowed")
    void overdraftWithinLimitAllowed() throws Exception {
        // Balance 500, overdraft 1000 → max withdrawal = 1500. Withdraw 1000 → balance -500
        checkingAccount.withdraw(1000.0);
        assertEquals(-500.0, checkingAccount.getBalance(), 0.001,
                "Withdrawal within overdraft limit should be allowed");
    }

    @Test
    @DisplayName("CheckingAccount.withdraw() — exceeding overdraft limit throws exception")
    void overdraftExceedThrowsException() {
        // Balance 500, overdraft 1000 → max = 1500. Withdraw 1600 → should throw
        assertThrows(org.example.model.exceptions.OverdraftExceededException.class,
                () -> checkingAccount.withdraw(1600.0),
                "Withdrawal exceeding overdraft limit must throw OverdraftExceededException");
    }

    // -------------------------------------------------------------------------
    // Interest calculation
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("SavingsAccount.calculateInterest() — computes 3.5% yearly interest correctly")
    void calculateInterestIsCorrect() {
        // Balance = 1000.0, rate = 3.5% → interest = 35.0
        double expectedInterest = 1000.0 * 0.035;
        assertEquals(expectedInterest, savingsAccount.calculateInterest(), 0.001,
                "Interest should be balance × (rate / 100)");
    }

    // -------------------------------------------------------------------------
    // Account type / number
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Account numbers are auto-generated in ACC### format")
    void accountNumberFormat() {
        assertTrue(savingsAccount.getAccountNumber().matches("ACC\\d{3}"),
                "Account number must match pattern ACC###");
    }

    @Test
    @DisplayName("getAccountType() returns correct type string")
    void accountTypeLabels() {
        assertEquals("Savings",  savingsAccount.getAccountType());
        assertEquals("Checking", checkingAccount.getAccountType());
    }
}
