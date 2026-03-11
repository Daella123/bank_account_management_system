package org.example;

public class TransactionManager {
    private Transaction[] transactions;
    private int transactionCount;
    
    public TransactionManager() {
        transactions = new Transaction[200];
        transactionCount = 0;
    }
    
    public boolean addTransaction(Transaction transaction) {
        if (transactionCount < transactions.length) {
            transactions[transactionCount] = transaction;
            transactionCount++;
            return true;
        }
        return false;
    }
    
    public void viewTransactionsByAccount(String accountNumber) {
        // Find all transactions for this account
        Transaction[] accountTransactions = new Transaction[transactionCount];
        int count = 0;
        
        // Linear search to find matching transactions
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i].getAccountNumber().equals(accountNumber)) {
                accountTransactions[count] = transactions[i];
                count++;
            }
        }
        
        if (count == 0) {
            System.out.println("\nNo transactions recorded for this account.");
            return;
        }
        
        System.out.println("\nTRANSACTION HISTORY\n");
        System.out.println("TXN ID | DATE/TIME           | TYPE       | AMOUNT      | BALANCE");
        System.out.println("-------|---------------------|------------|-------------|-------------");
        
        // Display in reverse chronological order (newest first)
        for (int i = count - 1; i >= 0; i--) {
            Transaction txn = accountTransactions[i];
            String amountStr;
            if (txn.getType().equalsIgnoreCase("DEPOSIT")) {
                amountStr = "+$" + String.format("%,.2f", txn.getAmount());
            } else {
                amountStr = "-$" + String.format("%,.2f", txn.getAmount());
            }
            
            System.out.printf("%-7s| %-20s| %-11s| %-12s| $%s%n",
                    txn.getTransactionId(),
                    txn.getTimestamp(),
                    txn.getType().toUpperCase(),
                    amountStr,
                    String.format("%,.2f", txn.getBalanceAfter()));
        }
        
        // Display summary
        double totalDeposits = calculateTotalDeposits(accountNumber);
        double totalWithdrawals = calculateTotalWithdrawals(accountNumber);
        double netChange = totalDeposits - totalWithdrawals;
        
        System.out.println("\nTotal Transactions: " + count);
        System.out.println("Total Deposits: $" + String.format("%,.2f", totalDeposits));
        System.out.println("Total Withdrawals: $" + String.format("%,.2f", totalWithdrawals));
        System.out.print("Net Change: ");
        if (netChange >= 0) {
            System.out.println("+$" + String.format("%,.2f", netChange));
        } else {
            System.out.println("-$" + String.format("%,.2f", Math.abs(netChange)));
        }
    }
    
    public double calculateTotalDeposits(String accountNumber) {
        double total = 0;
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i].getAccountNumber().equals(accountNumber) &&
                transactions[i].getType().equalsIgnoreCase("DEPOSIT")) {
                total += transactions[i].getAmount();
            }
        }
        return total;
    }
    
    public double calculateTotalWithdrawals(String accountNumber) {
        double total = 0;
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i].getAccountNumber().equals(accountNumber) &&
                transactions[i].getType().equalsIgnoreCase("WITHDRAWAL")) {
                total += transactions[i].getAmount();
            }
        }
        return total;
    }
    
    public int getTransactionCount() {
        return transactionCount;
    }
}
