package org.example;

public abstract class Account {
    private static int accountCounter = 0;
    
    private String accountNumber;
    private Customer customer;
    private double balance;
    private String status;
    
    public Account(Customer customer, double initialBalance) {
        accountCounter++;
        this.accountNumber = String.format("ACC%03d", accountCounter);
        this.customer = customer;
        this.balance = initialBalance;
        this.status = "Active";
    }
    
    // Getters and setters
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    public double getBalance() {
        return balance;
    }
    
    protected void setBalance(double balance) {
        this.balance = balance;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    // Methods
    public boolean deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            return true;
        }
        return false;
    }
    
    public boolean withdraw(double amount) {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }
    
    // Abstract methods
    public abstract void displayAccountDetails();
    public abstract String getAccountType();
}
