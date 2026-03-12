package org.example.model;

import org.example.model.Account;
import org.example.model.Customer;

public class SavingsAccount extends Account {
    private double interestRate;
    private double minimumBalance;
    
    public SavingsAccount(Customer customer, double initialBalance) {
        super(customer, initialBalance);
        this.interestRate = 3.5;
        this.minimumBalance = 500.0;
    }
    
    public double getInterestRate() {
        return interestRate;
    }
    
    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }
    
    public double getMinimumBalance() {
        return minimumBalance;
    }
    
    public void setMinimumBalance(double minimumBalance) {
        this.minimumBalance = minimumBalance;
    }
    
    @Override
    public boolean withdraw(double amount) {
        if (amount > 0 && getBalance() - amount >= minimumBalance) {
            setBalance(getBalance() - amount);
            return true;
        }
        return false;
    }
    
    public double calculateInterest() {
        return getBalance() * (interestRate / 100);
    }
    
    @Override
    public void displayAccountDetails() {
        System.out.println("Account Number: " + getAccountNumber());
        System.out.println("Customer: " + getCustomer().getName() + " (" + getCustomer().getCustomerType() + ")");
        System.out.println("Account Type: " + getAccountType());
        System.out.println("Balance: $" + String.format("%,.2f", getBalance()));
        System.out.println("Status: " + getStatus());
        System.out.println("Interest Rate: " + interestRate + "%");
        System.out.println("Minimum Balance: $" + String.format("%,.2f", minimumBalance));
    }
    
    @Override
    public String getAccountType() {
        return "Savings";
    }
}
