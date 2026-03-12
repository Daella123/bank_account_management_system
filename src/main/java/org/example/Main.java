package org.example;

import org.example.model.*;
import org.example.service.AccountManager;
import org.example.service.TransactionManager;

import java.util.Scanner;

public class Main {
    private static AccountManager accountManager = new AccountManager();
    private static TransactionManager transactionManager = new TransactionManager();
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        // Initialize with sample data
        initializeSampleData();
        
        // Main menu loop
        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = getIntInput("Enter choice: ");
            
            switch (choice) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    viewAccounts();
                    break;
                case 3:
                    processTransaction();
                    break;
                case 4:
                    viewTransactionHistory();
                    break;
                case 5:
                    System.out.println("\nThank you for using Bank Account Management System!");
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("\nInvalid choice. Please select 1-5.");
            }
            
            if (running && choice >= 1 && choice <= 4) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
        
        scanner.close();
    }
    
    private static void initializeSampleData() {
        // Create 5 sample accounts (3 Savings, 2 Checking)
        
        // Account 1: Savings - Regular Customer
        Customer customer1 = new RegularCustomer("Mpamo Avy", 35, "+250-789-331-259", "KG 480, kigali");
        Account account1 = new SavingsAccount(customer1, 5250.0);
        accountManager.addAccount(account1);
        
        // Account 2: Checking - Regular Customer
        Customer customer2 = new RegularCustomer("Igabe Lanuja", 28, "+250-782-471-299", "KG 340, kigali");
        Account account2 = new CheckingAccount(customer2, 3450.0);
        accountManager.addAccount(account2);
        
        // Account 3: Savings - Premium Customer
        Customer customer3 = new PremiumCustomer("Bigwi Axel", 45, "+250-781-437-239", "KG 261, kigali");
        Account account3 = new SavingsAccount(customer3, 15750.0);
        accountManager.addAccount(account3);
        
        // Account 4: Checking - Regular Customer
        Customer customer4 = new RegularCustomer("Ineza Annick", 31, "+250-788-831-282", "KG 453, kigali");
        Account account4 = new CheckingAccount(customer4, 880.0);
        accountManager.addAccount(account4);
        
        // Account 5: Savings - Premium Customer
        Customer customer5 = new PremiumCustomer("Nkota Leslie", 52, "+250-788-301-245", "KG 367, kigali");
        Account account5 = new SavingsAccount(customer5, 25200.0);
        accountManager.addAccount(account5);
    }
    
    private static void displayMainMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("BANK ACCOUNT MANAGEMENT - MAIN MENU");
        System.out.println("=".repeat(50));
        System.out.println("1. Create Account");
        System.out.println("2. View Accounts");
        System.out.println("3. Process Transaction");
        System.out.println("4. View Transaction History");
        System.out.println("5. Exit");
        System.out.println("=".repeat(50));
    }
    
    private static void createAccount() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("ACCOUNT CREATION");
        System.out.println("=".repeat(50));
        
        // Get customer details
        System.out.print("Enter customer name: ");
        String name = scanner.nextLine();
        
        int age = getIntInput("Enter customer age: ");
        
        System.out.print("Enter customer contact: ");
        String contact = scanner.nextLine();
        
        System.out.print("Enter customer address: ");
        String address = scanner.nextLine();
        
        // Select customer type
        System.out.println("\nCustomer type:");
        System.out.println("1. Regular Customer (Standard banking services)");
        System.out.println("2. Premium Customer (Enhanced benefits, min balance $10,000)");
        int customerType = getIntInputInRange("Select type (1-2): ", 1, 2);
        
        Customer customer;
        if (customerType == 2) {
            customer = new PremiumCustomer(name, age, contact, address);
        } else {
            customer = new RegularCustomer(name, age, contact, address);
        }
        
        // Select account type
        System.out.println("\nAccount type:");
        System.out.println("1. Savings Account (Interest: 3.5%, Min Balance: $500)");
        System.out.println("2. Checking Account (Overdraft: $1,000, Monthly Fee: $10)");
        int accountType = getIntInputInRange("Select type (1-2): ", 1, 2);
        
        // Get initial deposit
        double initialDeposit = getDoubleInput("Enter initial deposit amount: $");
        
        // Create account
        Account account;
        if (accountType == 1) {
            account = new SavingsAccount(customer, initialDeposit);
        } else {
            account = new CheckingAccount(customer, initialDeposit);
        }
        
        accountManager.addAccount(account);
        
        // Display confirmation
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Account created successfully!");
        System.out.println("=".repeat(50));
        account.displayAccountDetails();
        System.out.println("=".repeat(50));
    }
    
    private static void viewAccounts() {
        accountManager.viewAllAccounts();
    }
    
    private static void processTransaction() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("PROCESS TRANSACTION");
        System.out.println("=".repeat(50));
        
        System.out.print("Enter Account Number: ");
        String accountNumber = scanner.nextLine().trim();
        
        Account account = accountManager.findAccount(accountNumber);
        
        if (account == null) {
            System.out.println("\nError: Account not found!");
            return;
        }
        
        // Display account details
        System.out.println("\nAccount Details:");
        System.out.println("Customer: " + account.getCustomer().getName());
        System.out.println("Account Type: " + account.getAccountType());
        System.out.println("Current Balance: $" + String.format("%,.2f", account.getBalance()));
        
        // Select transaction type
        System.out.println("\nTransaction type:");
        System.out.println("1. Deposit");
        System.out.println("2. Withdrawal");
        int transactionType = getIntInputInRange("Select type (1-2): ", 1, 2);
        
        double amount = getDoubleInput("Enter amount: $");
        
        if (amount <= 0) {
            System.out.println("\nError: Amount must be positive!");
            return;
        }
        
        String type = (transactionType == 1) ? "DEPOSIT" : "WITHDRAWAL";
        double previousBalance = account.getBalance();
        boolean success = account.processTransaction(amount, type);
        
        if (!success) {
            System.out.println("\nError: Transaction failed!");
            if (transactionType == 2) {
                if (account instanceof SavingsAccount) {
                    SavingsAccount savingsAccount = (SavingsAccount) account;
                    System.out.println("Reason: Insufficient funds or minimum balance ($" + 
                            String.format("%,.2f", savingsAccount.getMinimumBalance()) + ") requirement not met.");
                } else {
                    System.out.println("Reason: Insufficient funds including overdraft limit.");
                }
            }
            return;
        }
        
        // Create transaction record
        Transaction transaction = new Transaction(accountNumber, type, amount, account.getBalance());
        
        // Display confirmation
        System.out.println("\n" + "=".repeat(50));
        transaction.displayTransactionDetails();
        System.out.println("Previous Balance: $" + String.format("%,.2f", previousBalance));
        System.out.println("=".repeat(50));
        
        System.out.print("\nConfirm transaction? (Y/N): ");
        String confirm = scanner.nextLine().trim();
        
        if (confirm.equalsIgnoreCase("Y")) {
            transactionManager.addTransaction(transaction);
            System.out.println("\nTransaction completed successfully!");
        } else {
            // Rollback transaction
            String rollbackType = (transactionType == 1) ? "WITHDRAWAL" : "DEPOSIT";
            account.processTransaction(amount, rollbackType);
            System.out.println("\nTransaction cancelled.");
        }
    }
    
    private static void viewTransactionHistory() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("VIEW TRANSACTION HISTORY");
        System.out.println("=".repeat(50));
        
        System.out.print("Enter Account Number: ");
        String accountNumber = scanner.nextLine().trim();
        
        Account account = accountManager.findAccount(accountNumber);
        
        if (account == null) {
            System.out.println("\nError: Account not found!");
            return;
        }
        
        System.out.println("\nAccount: " + accountNumber + " - " + account.getCustomer().getName());
        System.out.println("Account Type: " + account.getAccountType());
        System.out.println("Current Balance: $" + String.format("%,.2f", account.getBalance()));
        
        transactionManager.viewTransactionsByAccount(accountNumber);
    }
    
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
    
    private static int getIntInputInRange(String prompt, int min, int max) {
        while (true) {
            int value = getIntInput(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            System.out.println("Invalid choice. Please enter a number between " + min + " and " + max + ".");
        }
    }
    
    private static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                // Remove $ and commas if present
                input = input.replace("$", "").replace(",", "");
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid amount.");
            }
        }
    }
}