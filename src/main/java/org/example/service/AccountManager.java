package org.example.service;

import org.example.model.Account;
import org.example.model.CheckingAccount;
import org.example.model.SavingsAccount;
import org.example.model.exceptions.InvalidAccountException;

/**
 * Manages the in-memory collection of {@link Account} objects.
 * <p>
 * Uses a fixed-size array (capacity 50) to store accounts. Provides
 * operations for adding, finding, and listing all accounts.
 * </p>
 */
public class AccountManager {

    /** Maximum number of accounts that can be held in-memory. */
    private static final int MAX_ACCOUNTS = 50;

    private final Account[] accounts;
    private int accountCount;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /** Creates a new AccountManager with an empty account store. */
    public AccountManager() {
        accounts     = new Account[MAX_ACCOUNTS];
        accountCount = 0;
    }

    // -------------------------------------------------------------------------
    // CRUD operations
    // -------------------------------------------------------------------------

    /**
     * Adds an account to the store if capacity allows.
     *
     * @param account the account to add (must not be {@code null})
     * @return {@code true} if added successfully; {@code false} if the store is full
     */
    public boolean addAccount(Account account) {
        if (accountCount >= MAX_ACCOUNTS) return false;
        accounts[accountCount++] = account;
        return true;
    }

    /**
     * Finds an account by its account number using a linear search.
     *
     * @param accountNumber the account number to search for (e.g. {@code "ACC001"})
     * @return the matching {@link Account}
     * @throws InvalidAccountException if no account with that number exists
     */
    public Account findAccount(String accountNumber) throws InvalidAccountException {
        for (int i = 0; i < accountCount; i++) {
            if (accounts[i].getAccountNumber().equals(accountNumber)) {
                return accounts[i];
            }
        }
        throw new InvalidAccountException(
                "Account not found: \"" + accountNumber
                + "\". Please check the account number and try again.");
    }

    /**
     * Searches for an account by number without throwing — for internal/UI use.
     *
     * @param accountNumber the account number to search for
     * @return the matching {@link Account} or {@code null} if not found
     */
    public Account findAccountOrNull(String accountNumber) {
        for (int i = 0; i < accountCount; i++) {
            if (accounts[i].getAccountNumber().equals(accountNumber)) {
                return accounts[i];
            }
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // Reporting / display
    // -------------------------------------------------------------------------

    /**
     * Prints a formatted table of all accounts to {@code System.out}.
     */
    public void viewAllAccounts() {
        if (accountCount == 0) {
            System.out.println("No accounts found.");
            return;
        }

        System.out.println("\nACCOUNT LISTING\n");
        System.out.println("ACC NO  | CUSTOMER NAME          | TYPE     | BALANCE       | STATUS | DETAILS");
        System.out.println("--------|------------------------|----------|---------------|--------|----------------------------------");

        for (int i = 0; i < accountCount; i++) {
            printAccountRow(accounts[i]);
        }

        System.out.println("\nTotal Accounts     : " + accountCount);
        System.out.printf ("Total Bank Balance : $%,.2f%n", getTotalBalance());
    }

    /**
     * Prints a single account row in the account listing table.
     *
     * @param acc the account to print
     */
    private void printAccountRow(Account acc) {
        String details = buildTypeDetails(acc);
        System.out.printf("%-8s| %-23s| %-9s| $%-13s| %-7s| %s%n",
                acc.getAccountNumber(),
                acc.getCustomer().getName(),
                acc.getAccountType(),
                String.format("%,.2f", acc.getBalance()),
                acc.getStatus(),
                details);
    }

    /**
     * Builds a short detail string specific to the account subtype.
     *
     * @param acc the account
     * @return a formatted detail string
     */
    private String buildTypeDetails(Account acc) {
        if (acc instanceof SavingsAccount sa) {
            return String.format("MinBal: $%,.2f | Rate: %.1f%%",
                    sa.getMinimumBalance(), sa.getInterestRate());
        } else if (acc instanceof CheckingAccount ca) {
            return String.format("Overdraft: $%,.2f | Fee: %s",
                    ca.getOverdraftLimit(),
                    ca.isFeeWaived() ? "WAIVED" : String.format("$%.2f", ca.getMonthlyFee()));
        }
        return "";
    }

    // -------------------------------------------------------------------------
    // Aggregates
    // -------------------------------------------------------------------------

    /**
     * Returns the sum of all account balances.
     *
     * @return total balance across all accounts
     */
    public double getTotalBalance() {
        double total = 0;
        for (int i = 0; i < accountCount; i++) {
            total += accounts[i].getBalance();
        }
        return total;
    }

    /** @return the number of accounts currently stored */
    public int getAccountCount() { return accountCount; }

    /** @return a direct reference to the internal accounts array (read-only use) */
    public Account[] getAccounts() { return accounts; }
}
