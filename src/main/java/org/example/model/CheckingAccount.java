package org.example.model;

public class CheckingAccount extends Account {
    private double overdraftLimit;
    private double monthlyFee;
    
    public CheckingAccount(Customer customer, double initialBalance) {
        super(customer, initialBalance);
        this.overdraftLimit = 1000.0;
        this.monthlyFee = 10.0;
    }
    
    public double getOverdraftLimit() {
        return overdraftLimit;
    }
    
    public void setOverdraftLimit(double overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }
    
    public double getMonthlyFee() {
        return monthlyFee;
    }
    
    public void setMonthlyFee(double monthlyFee) {
        this.monthlyFee = monthlyFee;
    }
    
    @Override
    public boolean withdraw(double amount) {
        if (amount > 0 && getBalance() + overdraftLimit >= amount) {
            setBalance(getBalance() - amount);
            return true;
        }
        return false;
    }
    
    public void applyMonthlyFee() {
        // Check if customer is premium to waive fee
        if (getCustomer() instanceof PremiumCustomer) {
            PremiumCustomer premiumCustomer = (PremiumCustomer) getCustomer();
            if (premiumCustomer.hasWaivedFees()) {
                return; // Fee is waived
            }
        }
        setBalance(getBalance() - monthlyFee);
    }
    
    @Override
    public void displayAccountDetails() {
        System.out.println("Account Number: " + getAccountNumber());
        System.out.println("Customer: " + getCustomer().getName() + " (" + getCustomer().getCustomerType() + ")");
        System.out.println("Account Type: " + getAccountType());
        System.out.println("Balance: $" + String.format("%,.2f", getBalance()));
        System.out.println("Status: " + getStatus());
        
        // Check if monthly fee is waived for premium customers
        boolean feeWaived = false;
        if (getCustomer() instanceof PremiumCustomer) {
            PremiumCustomer premiumCustomer = (PremiumCustomer) getCustomer();
            feeWaived = premiumCustomer.hasWaivedFees();
        }
        
        if (feeWaived) {
            System.out.println("Monthly Fee: WAIVED");
        } else {
            System.out.println("Monthly Fee: $" + String.format("%.2f", monthlyFee));
        }
        System.out.println("Overdraft Limit: $" + String.format("%,.2f", overdraftLimit));
    }
    
    @Override
    public String getAccountType() {
        return "Checking";
    }
}
