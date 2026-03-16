package org.example;

import org.example.model.*;
import org.example.model.exceptions.*;
import org.example.service.AccountManager;
import org.example.service.TransactionManager;
import org.example.utils.ValidationUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests focused on verifying that all custom exception classes are thrown
 * correctly and carry the expected data (message, balance, limit, etc.).
 * <p>
 * Each test is named after the exception class it targets for clarity.
 * </p>
 */
@DisplayName("Exception Tests")
class ExceptionTest {

    // -------------------------------------------------------------------------
    // Fixtures
    // -------------------------------------------------------------------------

    private SavingsAccount     savingsAccount;
    private CheckingAccount    checkingAccount;
    private AccountManager     accountManager;
    private TransactionManager transactionManager;

    /** Creates fresh test doubles before every test. */
    @BeforeEach
    void setUp() {
        Customer regular = new RegularCustomer("Test",   28, "000", "Somewhere");
        Customer premium = new PremiumCustomer("Elite",  45, "111", "Elsewhere");

        savingsAccount     = new SavingsAccount(regular, 1000.0);
        checkingAccount    = new CheckingAccount(premium, 500.0);
        accountManager     = new AccountManager();
        transactionManager = new TransactionManager();

        accountManager.addAccount(savingsAccount);
        accountManager.addAccount(checkingAccount);
    }

    // -------------------------------------------------------------------------
    // InvalidAmountException
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("InvalidAmountException — thrown for zero deposit")
    void invalidAmountExceptionOnZeroDeposit() {
        InvalidAmountException ex = assertThrows(
                InvalidAmountException.class,
                () -> savingsAccount.deposit(0),
                "Deposit of 0 must throw InvalidAmountException");
        assertNotNull(ex.getMessage(), "Exception message must not be null");
    }

    @Test
    @DisplayName("InvalidAmountException — thrown for negative withdrawal")
    void invalidAmountExceptionOnNegativeWithdraw() {
        assertThrows(InvalidAmountException.class,
                () -> savingsAccount.withdraw(-50.0),
                "Negative withdrawal must throw InvalidAmountException");
    }

    @Test
    @DisplayName("ValidationUtils.validateAmount() — throws for non-positive value")
    void validationUtilsThrowsForNonPositive() {
        assertThrows(InvalidAmountException.class,
                () -> ValidationUtils.validateAmount(-1.0));
        assertThrows(InvalidAmountException.class,
                () -> ValidationUtils.validateAmount(0.0));
    }

    // -------------------------------------------------------------------------
    // InsufficientFundsException
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("InsufficientFundsException — thrown when withdrawal violates min balance")
    void insufficientFundsExceptionThrown() {
        // Balance 1000, min 500 → withdraw 600 → 400 < 500 → throw
        InsufficientFundsException ex = assertThrows(
                InsufficientFundsException.class,
                () -> savingsAccount.withdraw(600.0),
                "Withdraw below minimum must throw InsufficientFundsException");

        assertEquals(1000.0, ex.getCurrentBalance(), 0.001,
                "Exception should carry the balance at time of failure");
    }

    @Test
    @DisplayName("InsufficientFundsException — message is descriptive")
    void insufficientFundsExceptionHasMessage() {
        InsufficientFundsException ex = assertThrows(
                InsufficientFundsException.class,
                () -> savingsAccount.withdraw(600.0));
        assertFalse(ex.getMessage().isBlank(), "Exception message must not be blank");
    }

    // -------------------------------------------------------------------------
    // OverdraftExceededException
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("OverdraftExceededException — thrown when withdrawal exceeds overdraft limit")
    void overdraftExceededExceptionThrown() {
        // Balance 500, overdraft 1000 → max 1500. Withdraw 1600 → throw
        OverdraftExceededException ex = assertThrows(
                OverdraftExceededException.class,
                () -> checkingAccount.withdraw(1600.0),
                "Exceeding overdraft limit must throw OverdraftExceededException");

        assertEquals(1000.0, ex.getOverdraftLimit(), 0.001,
                "Exception should carry the overdraft limit");
    }

    @Test
    @DisplayName("OverdraftExceededException — not thrown when within limit")
    void noExceptionWithinOverdraftLimit() {
        // Balance 500, overdraft 1000 → withdraw 1000 → balance -500 → allowed
        assertDoesNotThrow(() -> checkingAccount.withdraw(1000.0),
                "Withdrawal within overdraft limit must NOT throw");
    }

    // -------------------------------------------------------------------------
    // InvalidAccountException
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("InvalidAccountException — thrown for unknown account number")
    void invalidAccountExceptionThrown() throws InvalidAccountException {
        InvalidAccountException ex = assertThrows(
                InvalidAccountException.class,
                () -> accountManager.findAccount("ACC999"),
                "Looking up a non-existent account must throw InvalidAccountException");
        assertNotNull(ex.getMessage(), "Exception message must not be null");
    }

    @Test
    @DisplayName("InvalidAccountException — NOT thrown for known account")
    void validAccountDoesNotThrow() {
        assertDoesNotThrow(
                () -> accountManager.findAccount(savingsAccount.getAccountNumber()),
                "Finding a valid account must NOT throw InvalidAccountException");
    }

    @Test
    @DisplayName("InvalidAccountException — thrown on same-account transfer")
    void samAccountTransferThrows() {
        assertThrows(InvalidAccountException.class,
                () -> transactionManager.transfer(savingsAccount, savingsAccount, 100.0),
                "Transfer to self must throw InvalidAccountException");
    }

    // -------------------------------------------------------------------------
    // ValidationUtils edge cases
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("ValidationUtils.validateAge() — throws for negative age")
    void validateAgeThrowsForNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.validateAge(-1));
    }

    @Test
    @DisplayName("ValidationUtils.validateName() — throws for blank name")
    void validateNameThrowsForBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.validateName("", "Customer Name"));
    }
}
