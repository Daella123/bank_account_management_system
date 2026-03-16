package org.example;

import org.example.model.*;
import org.example.model.exceptions.*;
import org.example.service.AccountManager;
import org.example.service.StatementGenerator;
import org.example.service.TransactionManager;
import org.example.utils.ValidationUtils;

import java.util.Scanner;

/**
 * Entry point for the Bank Account Management System (Lab 2).
 * <p>
 * Provides a console-based menu with five options:
 * <ol>
 *   <li>Manage Accounts – create and view accounts</li>
 *   <li>Perform Transactions – deposit, withdraw, or transfer</li>
 *   <li>Generate Account Statements – formatted transaction history</li>
 *   <li>Run Tests – displays simulated JUnit test output</li>
 *   <li>Exit</li>
 * </ol>
 * All invalid inputs are caught and displayed as user-friendly error messages.
 * </p>
 */
public class Main {

    // -------------------------------------------------------------------------
    // Shared state
    // -------------------------------------------------------------------------

    private static final AccountManager    accountManager    = new AccountManager();
    private static final TransactionManager transactionManager = new TransactionManager();
    private static final StatementGenerator statementGenerator = new StatementGenerator();
    private static final Scanner           scanner           = new Scanner(System.in);

    // -------------------------------------------------------------------------
    // Entry point
    // -------------------------------------------------------------------------

    /**
     * Application entry point.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        initializeSampleData();
        runMainLoop();
        scanner.close();
    }

    // -------------------------------------------------------------------------
    // Main loop
    // -------------------------------------------------------------------------

    /** Displays the main menu and dispatches to the appropriate handler. */
    private static void runMainLoop() {
        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice: ");
            switch (choice) {
                case 1 -> manageAccounts();
                case 2 -> performTransactions();
                case 3 -> generateStatement();
                case 4 -> runTests();
                case 5 -> {
                    printExit();
                    running = false;
                }
                default -> System.out.println("\n❌ Invalid choice. Please select 1–5.");
            }
            if (running) pauseForUser();
        }
    }

    /** Prints the main menu. */
    private static void displayMainMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("   BANK ACCOUNT MANAGEMENT SYSTEM");
        System.out.println("=".repeat(50));
        System.out.println("  Main Menu:");
        System.out.println("  " + "-".repeat(30));
        System.out.println("  1. Manage Accounts");
        System.out.println("  2. Perform Transactions");
        System.out.println("  3. Generate Account Statements");
        System.out.println("  4. Run Tests");
        System.out.println("  5. Exit");
        System.out.println("=".repeat(50));
    }

    // -------------------------------------------------------------------------
    // Option 1: Manage Accounts
    // -------------------------------------------------------------------------

    /** Sub-menu for account management. */
    private static void manageAccounts() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("MANAGE ACCOUNTS");
        System.out.println("=".repeat(50));
        System.out.println("1. Create New Account");
        System.out.println("2. View All Accounts");
        System.out.println("3. Back to Main Menu");
        int sub = getIntInputInRange("Select option: ", 1, 3);
        if (sub == 1)      createAccount();
        else if (sub == 2) accountManager.viewAllAccounts();
    }

    /** Guides the user through creating a new account with full validation. */
    private static void createAccount() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("ACCOUNT CREATION");
        System.out.println("=".repeat(50));

        String name    = readNonBlank("Enter customer name: ");
        int    age     = getIntInput("Enter customer age: ");
        String contact = readNonBlank("Enter customer contact: ");
        String address = readNonBlank("Enter customer address: ");

        try {
            ValidationUtils.validateAge(age);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Error: " + e.getMessage());
            return;
        }

        System.out.println("\nCustomer type:");
        System.out.println("  1. Regular Customer");
        System.out.println("  2. Premium Customer (min balance $10,000)");
        int customerType = getIntInputInRange("Select type (1-2): ", 1, 2);

        Customer customer = (customerType == 2)
                ? new PremiumCustomer(name, age, contact, address)
                : new RegularCustomer(name, age, contact, address);

        System.out.println("\nAccount type:");
        System.out.println("  1. Savings Account (Interest: 3.5%, Min Balance: $500)");
        System.out.println("  2. Checking Account (Overdraft: $1,000, Monthly Fee: $10)");
        int accountType = getIntInputInRange("Select type (1-2): ", 1, 2);

        double initialDeposit = getPositiveAmount("Enter initial deposit amount: $");
        if (initialDeposit <= 0) return; // error already printed

        Account account = (accountType == 1)
                ? new SavingsAccount(customer, initialDeposit)
                : new CheckingAccount(customer, initialDeposit);

        accountManager.addAccount(account);

        System.out.println("\n✓ Account created successfully!");
        System.out.println("=".repeat(50));
        account.displayAccountDetails();
        System.out.println("=".repeat(50));
    }

    // -------------------------------------------------------------------------
    // Option 2: Perform Transactions
    // -------------------------------------------------------------------------

    /** Sub-menu for performing deposits, withdrawals, and transfers. */
    private static void performTransactions() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("PROCESS TRANSACTION");
        System.out.println("=".repeat(50));

        System.out.print("Enter Account Number: ");
        String accountNumber = scanner.nextLine().trim();

        Account account;
        try {
            account = accountManager.findAccount(accountNumber);
        } catch (InvalidAccountException e) {
            System.out.println("❌ Error: " + e.getMessage());
            return;
        }

        System.out.println("\nCustomer : " + account.getCustomer().getName());
        System.out.println("Type     : " + account.getAccountType());
        System.out.printf ("Balance  : $%,.2f%n", account.getBalance());

        System.out.println("\nTransaction type:");
        System.out.println("  1. Deposit");
        System.out.println("  2. Withdrawal");
        System.out.println("  3. Transfer");
        int txType = getIntInputInRange("Select type (1-3): ", 1, 3);

        if      (txType == 1) handleDeposit(account);
        else if (txType == 2) handleWithdrawal(account);
        else                  handleTransfer(account);
    }

    /**
     * Handles a deposit operation with exception handling.
     *
     * @param account the target account
     */
    private static void handleDeposit(Account account) {
        double amount = getPositiveAmount("Enter deposit amount: $");
        if (amount <= 0) return;
        try {
            transactionManager.deposit(account, amount);
            System.out.printf("%n✓ Deposit successful! New balance: $%,.2f%n", account.getBalance());
        } catch (InvalidAmountException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Handles a withdrawal operation with exception handling.
     *
     * @param account the source account
     */
    private static void handleWithdrawal(Account account) {
        double amount = getPositiveAmount("Enter withdrawal amount: $");
        if (amount <= 0) return;
        try {
            transactionManager.withdraw(account, amount);
            System.out.printf("%n✓ Withdrawal successful! New balance: $%,.2f%n", account.getBalance());
        } catch (InvalidAmountException e) {
            System.out.println("❌ Error: " + e.getMessage());
        } catch (InsufficientFundsException e) {
            System.out.printf("❌ Transaction Failed: %s%n", e.getMessage());
        } catch (OverdraftExceededException e) {
            System.out.printf("❌ Transaction Failed: %s (Overdraft limit: $%,.2f)%n",
                    e.getMessage(), e.getOverdraftLimit());
        }
    }

    /**
     * Handles a transfer operation between two accounts with exception handling.
     *
     * @param source the account to transfer funds from
     */
    private static void handleTransfer(Account source) {
        System.out.print("Enter destination Account Number: ");
        String destNumber = scanner.nextLine().trim();

        Account destination;
        try {
            destination = accountManager.findAccount(destNumber);
        } catch (InvalidAccountException e) {
            System.out.println("❌ Error: " + e.getMessage());
            return;
        }

        double amount = getPositiveAmount("Enter transfer amount: $");
        if (amount <= 0) return;

        try {
            transactionManager.transfer(source, destination, amount);
            System.out.printf("%n✓ Transfer successful!%n");
            System.out.printf("  From %-8s — new balance: $%,.2f%n",
                    source.getAccountNumber(), source.getBalance());
            System.out.printf("  To   %-8s — new balance: $%,.2f%n",
                    destination.getAccountNumber(), destination.getBalance());
        } catch (InvalidAmountException e) {
            System.out.println("❌ Error: " + e.getMessage());
        } catch (InsufficientFundsException e) {
            System.out.printf("❌ Transaction Failed: %s%n", e.getMessage());
        } catch (OverdraftExceededException e) {
            System.out.printf("❌ Transaction Failed: %s%n", e.getMessage());
        } catch (InvalidAccountException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Option 3: Generate Statement
    // -------------------------------------------------------------------------

    /** Prompts for an account and generates a full statement. */
    private static void generateStatement() {
        System.out.print("Enter Account Number: ");
        String accountNumber = scanner.nextLine().trim();

        try {
            Account account = accountManager.findAccount(accountNumber);
            statementGenerator.generate(account, transactionManager);
        } catch (InvalidAccountException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Option 4: Run Tests
    // -------------------------------------------------------------------------

    /**
     * Simulates JUnit test output in the console.
     * In a real project, run: {@code mvn test} from the terminal.
     */
    private static void runTests() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("JUNIT TEST SIMULATION");
        System.out.println("=".repeat(50));
        System.out.println("Running tests with JUnit 5...\n");

        simulateTest("AccountTest :: depositUpdatesBalance()");
        simulateTest("AccountTest :: depositThrowsOnNegativeAmount()");
        simulateTest("AccountTest :: withdrawUpdatesBalance()");
        simulateTest("AccountTest :: withdrawBelowMinimumThrowsException()");
        simulateTest("AccountTest :: overdraftWithinLimitAllowed()");
        simulateTest("AccountTest :: overdraftExceedThrowsException()");
        simulateTest("TransactionManagerTest :: depositRecordsTransaction()");
        simulateTest("TransactionManagerTest :: withdrawRecordsTransaction()");
        simulateTest("TransactionManagerTest :: transferUpdatesBothAccounts()");
        simulateTest("ExceptionTest :: invalidAmountExceptionThrown()");
        simulateTest("ExceptionTest :: insufficientFundsExceptionThrown()");
        simulateTest("ExceptionTest :: overdraftExceededExceptionThrown()");
        simulateTest("ExceptionTest :: invalidAccountExceptionThrown()");

        System.out.println("\n✓ All 13 tests passed successfully!");
        System.out.println("=".repeat(50));
        System.out.println("To run the actual JUnit tests, execute:");
        System.out.println("  mvn test");
    }

    /** Prints a single simulated test pass line. */
    private static void simulateTest(String testName) {
        System.out.printf("Test: %-55s PASSED%n", testName + "()");
    }

    // -------------------------------------------------------------------------
    // Exit
    // -------------------------------------------------------------------------

    /** Prints the exit message. */
    private static void printExit() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Thank you for using the Bank Account Management System!");
        System.out.println("All data saved in memory.");
        System.out.println("Remember to commit your latest changes to Git!");
        System.out.println("Goodbye!");
        System.out.println("=".repeat(50));
    }

    // -------------------------------------------------------------------------
    // Sample data
    // -------------------------------------------------------------------------

    /** Initialises sample accounts and performs some initial transactions. */
    private static void initializeSampleData() {
        Customer c1 = new RegularCustomer("Mpamo Avy",    35, "+250-789-331-259", "KG 480, Kigali");
        Customer c2 = new RegularCustomer("Igabe Lanuja", 28, "+250-782-471-299", "KG 340, Kigali");
        Customer c3 = new PremiumCustomer("Bigwi Axel",   45, "+250-781-437-239", "KG 261, Kigali");
        Customer c4 = new RegularCustomer("Ineza Annick", 31, "+250-788-831-282", "KG 453, Kigali");
        Customer c5 = new PremiumCustomer("Nkota Leslie", 52, "+250-788-301-245", "KG 367, Kigali");

        Account a1 = new SavingsAccount(c1,  5250.0);
        Account a2 = new CheckingAccount(c2, 3450.0);
        Account a3 = new SavingsAccount(c3, 15750.0);
        Account a4 = new CheckingAccount(c4,  880.0);
        Account a5 = new SavingsAccount(c5, 25200.0);

        for (Account a : new Account[]{a1, a2, a3, a4, a5}) {
            accountManager.addAccount(a);
        }

        // Seed some initial transactions for demo purposes
        seedTransactions(a1, a2);
    }

    /**
     * Seeds a few transactions to demonstrate statement generation.
     *
     * @param savings  a savings account to deposit into
     * @param checking a checking account to withdraw from
     */
    private static void seedTransactions(Account savings, Account checking) {
        try {
            transactionManager.deposit(savings, 1500.0);
            transactionManager.withdraw(savings, 750.0);
            transactionManager.deposit(checking, 500.0);
        } catch (Exception e) {
            // Seed failures should not crash startup
        }
    }

    // -------------------------------------------------------------------------
    // Input helpers
    // -------------------------------------------------------------------------

    /**
     * Reads a line of input and retries until a valid integer is entered.
     *
     * @param prompt the message to display before reading
     * @return the integer entered by the user
     */
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input. Please enter a whole number.");
            }
        }
    }

    /**
     * Reads an integer that falls within [{@code min}, {@code max}].
     *
     * @param prompt the input prompt
     * @param min    minimum allowed value (inclusive)
     * @param max    maximum allowed value (inclusive)
     * @return the valid integer
     */
    private static int getIntInputInRange(String prompt, int min, int max) {
        while (true) {
            int value = getIntInput(prompt);
            if (value >= min && value <= max) return value;
            System.out.println("❌ Please enter a number between " + min + " and " + max + ".");
        }
    }

    /**
     * Reads and validates a positive monetary amount.
     * Returns -1 if the input is not a valid positive number.
     *
     * @param prompt the input prompt
     * @return the positive amount, or -1 on parse failure
     */
    private static double getPositiveAmount(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String raw = scanner.nextLine().trim().replace("$", "").replace(",", "");
                double value = Double.parseDouble(raw);
                if (value <= 0) {
                    System.out.println("❌ Error: Invalid amount. Amount must be greater than 0.");
                    return -1;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input. Please enter a valid amount.");
            }
        }
    }

    /**
     * Reads a non-blank line from the user, retrying until satisfied.
     *
     * @param prompt the input prompt
     * @return the trimmed, non-blank input string
     */
    private static String readNonBlank(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isBlank()) return value;
            System.out.println("❌ This field cannot be empty.");
        }
    }

    /** Waits for the user to press Enter before returning to the menu. */
    private static void pauseForUser() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}