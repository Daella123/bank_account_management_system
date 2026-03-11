package org.example;

public class AccountManager {
    private Account[] accounts;
    private int accountCount;
    
    public AccountManager() {
        accounts = new Account[50];
        accountCount = 0;
    }
    
    public boolean addAccount(Account account) {
        if (accountCount < accounts.length) {
            accounts[accountCount] = account;
            accountCount++;
            return true;
        }
        return false;
    }
    
    public Account findAccount(String accountNumber) {
        // Linear search algorithm
        for (int i = 0; i < accountCount; i++) {
            if (accounts[i].getAccountNumber().equals(accountNumber)) {
                return accounts[i];
            }
        }
        return null;
    }
    
    public void viewAllAccounts() {
        if (accountCount == 0) {
            System.out.println("No accounts found.");
            return;
        }
        
        System.out.println("\nACCOUNT LISTING\n");
        System.out.println("ACC NO | CUSTOMER NAME          | TYPE     | BALANCE      | STATUS | TYPE DETAILS");
        System.out.println("-------|------------------------|----------|--------------|--------|-----------------------------");
        
        for (int i = 0; i < accountCount; i++) {
            Account acc = accounts[i];
            String typeDetails = "";
            
            if (acc instanceof SavingsAccount) {
                SavingsAccount savingsAcc = (SavingsAccount) acc;
                typeDetails = String.format("MinBal: $%,.2f, Rate: %.2f%%", 
                        savingsAcc.getMinimumBalance(), 
                        savingsAcc.getInterestRate());
            } else if (acc instanceof CheckingAccount) {
                CheckingAccount checkingAcc = (CheckingAccount) acc;
                typeDetails = String.format("Overdraft: $%,.2f, Fee: $%.2f", 
                        checkingAcc.getOverdraftLimit(), 
                        checkingAcc.getMonthlyFee());
            }
            
            System.out.printf("%-7s| %-23s| %-9s| $%-12s| %-7s| %s%n",
                    acc.getAccountNumber(),
                    acc.getCustomer().getName(),
                    acc.getAccountType(),
                    String.format("%,.2f", acc.getBalance()),
                    acc.getStatus(),
                    typeDetails);
        }
        
        System.out.println("\nTotal Accounts: " + accountCount);
        System.out.println("Total Bank Balance: $" + String.format("%,.2f", getTotalBalance()));
    }
    
    public double getTotalBalance() {
        double total = 0;
        for (int i = 0; i < accountCount; i++) {
            total += accounts[i].getBalance();
        }
        return total;
    }
    
    public int getAccountCount() {
        return accountCount;
    }
}
