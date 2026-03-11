package org.example;

public class PremiumCustomer extends Customer {
    private double minimumBalance;
    
    public PremiumCustomer(String name, int age, String contact, String address) {
        super(name, age, contact, address);
        this.minimumBalance = 10000.0;
    }
    
    public double getMinimumBalance() {
        return minimumBalance;
    }
    
    public void setMinimumBalance(double minimumBalance) {
        this.minimumBalance = minimumBalance;
    }
    
    public boolean hasWaivedFees() {
        return true;
    }
    
    @Override
    public void displayCustomerDetails() {
        System.out.println("Customer ID: " + getCustomerId());
        System.out.println("Name: " + getName());
        System.out.println("Age: " + getAge());
        System.out.println("Contact: " + getContact());
        System.out.println("Address: " + getAddress());
        System.out.println("Type: " + getCustomerType());
        System.out.println("Premium Benefits: Higher transaction limits, Waived fees, Priority service");
        System.out.println("Minimum Balance Requirement: $" + String.format("%,.2f", minimumBalance));
    }
    
    @Override
    public String getCustomerType() {
        return "Premium";
    }
}
