package org.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private static int transactionCounter = 0;
    
    private String transactionId;
    private String accountNumber;
    private String type;
    private double amount;
    private double balanceAfter;
    private String timestamp;
    
    public Transaction(String accountNumber, String type, double amount, double balanceAfter) {
        transactionCounter++;
        this.transactionId = String.format("TXN%03d", transactionCounter);
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        
        // Generate timestamp
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
        this.timestamp = now.format(formatter);
    }
    
    // Getters
    public String getTransactionId() {
        return transactionId;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public String getType() {
        return type;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public double getBalanceAfter() {
        return balanceAfter;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void displayTransactionDetails() {
        System.out.println("\nTRANSACTION CONFIRMATION");
        System.out.println("Transaction ID: " + transactionId);
        System.out.println("Account: " + accountNumber);
        System.out.println("Type: " + type);
        System.out.println("Amount: $" + String.format("%,.2f", amount));
        System.out.println("Balance After Transaction: $" + String.format("%,.2f", balanceAfter));
        System.out.println("Date/Time: " + timestamp);
    }
}
