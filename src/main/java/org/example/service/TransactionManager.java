package org.example.service;

import org.example.model.Account;
import org.example.model.Transaction;
import org.example.model.exceptions.InvalidAmountException;
import org.example.model.exceptions.InsufficientFundsException;
import org.example.model.exceptions.InvalidAccountException;
import org.example.model.exceptions.OverdraftExceededException;

/**
 * Manages all transaction records and provides deposit, withdrawal,
 * and transfer operations with full exception handling.
 * <p>
 * Stores up to 200 transactions in memory using an array.
 * All financial operations delegate to the appropriate {@link Account} methods,
 * keeping business logic in the model layer.
 * </p>
 */
public class TransactionManager {

    /** Maximum number of transactions that can be stored in memory. */
    private static final int MAX_TRANSACTIONS = 200;

    private final Transaction[] transactions;
    private int transactionCount;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /** Creates a new TransactionManager with an empty transaction store. */
    public TransactionManager() {
        transactions     = new Transaction[MAX_TRANSACTIONS];
        transactionCount = 0;
    }

    // -------------------------------------------------------------------------
    // Transaction recording
    // -------------------------------------------------------------------------

    /**
     * Appends a {@link Transaction} to the in-memory store.
     *
     * @param transaction the transaction to record
     * @return {@code true} if stored successfully; {@code false} if the store is full
     */
    public boolean addTransaction(Transaction transaction) {
        if (transactionCount >= MAX_TRANSACTIONS) return false;
        transactions[transactionCount++] = transaction;
        return true;
    }

    // -------------------------------------------------------------------------
    // Financial operations
    // -------------------------------------------------------------------------

    /**
     * Deposits {@code amount} into {@code account} and records the transaction.
     *
     * @param account the target account
     * @param amount  the positive amount to deposit
     * @return the recorded {@link Transaction}
     * @throws InvalidAmountException if {@code amount} is zero or negative
     */
    public Transaction deposit(Account account, double amount)
            throws InvalidAmountException {
        account.deposit(amount);
        Transaction txn = new Transaction(account.getAccountNumber(), "DEPOSIT", amount, account.getBalance());
        addTransaction(txn);
        return txn;
    }

    /**
     * Withdraws {@code amount} from {@code account} and records the transaction.
     *
     * @param account the source account
     * @param amount  the positive amount to withdraw
     * @return the recorded {@link Transaction}
     * @throws InvalidAmountException      if {@code amount} is zero or negative
     * @throws InsufficientFundsException  if the account has insufficient funds
     * @throws OverdraftExceededException  if the withdrawal exceeds the overdraft limit
     *                                     (for checking accounts)
     */
    public Transaction withdraw(Account account, double amount)
            throws InvalidAmountException, InsufficientFundsException,
                   OverdraftExceededException {
        account.withdraw(amount);
        Transaction txn = new Transaction(account.getAccountNumber(), "WITHDRAWAL", amount, account.getBalance());
        addTransaction(txn);
        return txn;
    }

    /**
     * Transfers {@code amount} from {@code source} to {@code destination} and
     * records both the debit and credit transactions atomically.
     *
     * @param source      the account to debit
     * @param destination the account to credit
     * @param amount      the positive amount to transfer
     * @throws InvalidAmountException      if {@code amount} is zero or negative
     * @throws InsufficientFundsException  if the source has insufficient funds
     * @throws OverdraftExceededException  if the debit exceeds the overdraft limit
     * @throws InvalidAccountException     if source and destination are the same account
     */
    public void transfer(Account source, Account destination, double amount)
            throws InvalidAmountException, InsufficientFundsException,
                   OverdraftExceededException, InvalidAccountException {
        validateTransferAccounts(source, destination);
        source.withdraw(amount);
        destination.deposit(amount);
        recordTransfer(source, destination, amount);
    }

    /**
     * Validates that a transfer is between two different accounts.
     *
     * @param source      the debit account
     * @param destination the credit account
     * @throws InvalidAccountException if both accounts are the same
     */
    private void validateTransferAccounts(Account source, Account destination)
            throws InvalidAccountException {
        if (source.getAccountNumber().equals(destination.getAccountNumber())) {
            throw new InvalidAccountException(
                    "Transfer failed: source and destination accounts cannot be the same.");
        }
    }

    /**
     * Records a debit and a credit transaction for a completed transfer.
     *
     * @param source      the account that was debited
     * @param destination the account that was credited
     * @param amount      the transfer amount
     */
    private void recordTransfer(Account source, Account destination, double amount) {
        String note = "→ " + destination.getAccountNumber();
        addTransaction(new Transaction(source.getAccountNumber(),
                "TRANSFER_OUT(" + note + ")", amount, source.getBalance()));
        addTransaction(new Transaction(destination.getAccountNumber(),
                "TRANSFER_IN(← " + source.getAccountNumber() + ")",
                amount, destination.getBalance()));
    }

    // -------------------------------------------------------------------------
    // Querying & display
    // -------------------------------------------------------------------------

    /**
     * Prints all transactions for {@code accountNumber} in reverse chronological
     * order (newest first), followed by a summary.
     *
     * @param accountNumber the account number to filter by
     */
    public void viewTransactionsByAccount(String accountNumber) {
        Transaction[] accountTxns = filterByAccount(accountNumber);
        int count = countNonNull(accountTxns);

        if (count == 0) {
            System.out.println("\nNo transactions recorded for this account.");
            return;
        }

        printTransactionTable(accountTxns, count);
        printTransactionSummary(accountNumber, count);
    }

    /**
     * Returns an array of transactions belonging to the given account.
     *
     * @param accountNumber the account to filter by
     * @return array (may contain trailing nulls)
     */
    private Transaction[] filterByAccount(String accountNumber) {
        Transaction[] result = new Transaction[transactionCount];
        int idx = 0;
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i].getAccountNumber().equals(accountNumber)) {
                result[idx++] = transactions[i];
            }
        }
        return result;
    }

    /**
     * Counts non-null entries at the start of {@code arr}.
     *
     * @param arr an array that may have trailing nulls
     * @return number of non-null elements
     */
    private int countNonNull(Transaction[] arr) {
        int count = 0;
        for (Transaction t : arr) { if (t != null) count++; }
        return count;
    }

    /**
     * Prints the transaction table header and rows in reverse order.
     *
     * @param txns  the filtered transactions array
     * @param count the number of valid entries
     */
    private void printTransactionTable(Transaction[] txns, int count) {
        System.out.println("\nTRANSACTION HISTORY\n");
        System.out.println("TXN ID  | DATE/TIME           | TYPE              | AMOUNT       | BALANCE");
        System.out.println("--------|---------------------|-------------------|--------------|-------------");
        for (int i = count - 1; i >= 0; i--) {
            printTransactionRow(txns[i]);
        }
    }

    /**
     * Prints a single row in the transaction table.
     *
     * @param txn the transaction to print
     */
    private void printTransactionRow(Transaction txn) {
        String sign = txn.getType().startsWith("DEPOSIT")
                      || txn.getType().startsWith("TRANSFER_IN") ? "+" : "-";
        String amountStr = sign + "$" + String.format("%,.2f", txn.getAmount());
        System.out.printf("%-8s| %-20s| %-18s| %-13s| $%s%n",
                txn.getTransactionId(),
                txn.getTimestamp(),
                txn.getType(),
                amountStr,
                String.format("%,.2f", txn.getBalanceAfter()));
    }

    /**
     * Prints the deposit/withdrawal summary and net change for an account.
     *
     * @param accountNumber the account to summarise
     * @param count         the number of transactions found
     */
    private void printTransactionSummary(String accountNumber, int count) {
        double totalDeposits    = calculateTotalDeposits(accountNumber);
        double totalWithdrawals = calculateTotalWithdrawals(accountNumber);
        double netChange        = totalDeposits - totalWithdrawals;

        System.out.println("\nTotal Transactions : " + count);
        System.out.printf ("Total Deposits     : $%,.2f%n", totalDeposits);
        System.out.printf ("Total Withdrawals  : $%,.2f%n", totalWithdrawals);
        System.out.print  ("Net Change         : ");
        System.out.printf ("%s$%,.2f%n", netChange >= 0 ? "+" : "-", Math.abs(netChange));
    }

    // -------------------------------------------------------------------------
    // Aggregates
    // -------------------------------------------------------------------------

    /**
     * Calculates total deposit amounts for a given account.
     *
     * @param accountNumber the account to total
     * @return sum of all deposit amounts
     */
    public double calculateTotalDeposits(String accountNumber) {
        double total = 0;
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i].getAccountNumber().equals(accountNumber)
                    && transactions[i].getType().startsWith("DEPOSIT")) {
                total += transactions[i].getAmount();
            }
        }
        return total;
    }

    /**
     * Calculates total withdrawal amounts for a given account.
     *
     * @param accountNumber the account to total
     * @return sum of all withdrawal amounts
     */
    public double calculateTotalWithdrawals(String accountNumber) {
        double total = 0;
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i].getAccountNumber().equals(accountNumber)
                    && (transactions[i].getType().startsWith("WITHDRAWAL")
                        || transactions[i].getType().startsWith("TRANSFER_OUT"))) {
                total += transactions[i].getAmount();
            }
        }
        return total;
    }

    /** @return total number of recorded transactions */
    public int getTransactionCount() { return transactionCount; }

    /** @return direct reference to the transaction array (read-only use) */
    public Transaction[] getTransactions() { return transactions; }
}
