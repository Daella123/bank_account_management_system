# Bank Account Management System

A console-based **Bank Account Management System** built in Java 21.
This is the **Lab 2** version, which extends Lab 1 with:

- ✅ **Clean Code Refactoring** — modular methods, JavaDoc, Google Java Style Guide
- ✅ **Custom Exception Handling** — 4 custom exception classes
- ✅ **JUnit 5 Unit Tests** — 3 test classes, 20+ test cases
- ✅ **Enhanced Console UI** — 5-option menu with emoji error messages

---

## Project Structure

```
src/
├── main/java/org/example/
│   ├── Main.java                          ← Entry point (5-option menu)
│   ├── contract/
│   │   └── Transactable.java
│   ├── model/
│   │   ├── Account.java                   ← Abstract base class (refactored)
│   │   ├── SavingsAccount.java            ← Enforces minimum balance
│   │   ├── CheckingAccount.java           ← Supports overdraft
│   │   ├── Customer.java
│   │   ├── RegularCustomer.java
│   │   ├── PremiumCustomer.java
│   │   ├── Transaction.java
│   │   └── exceptions/
│   │       ├── InvalidAmountException.java
│   │       ├── InsufficientFundsException.java
│   │       ├── InvalidAccountException.java
│   │       └── OverdraftExceededException.java
│   ├── service/
│   │   ├── AccountManager.java            ← Manages account storage
│   │   ├── TransactionManager.java        ← Handles all financial operations
│   │   └── StatementGenerator.java        ← Generates formatted statements
│   └── utils/
│       └── ValidationUtils.java           ← Reusable validation helpers
│
└── test/java/org/example/
    ├── AccountTest.java                   ← 11 tests for Account hierarchy
    ├── TransactionManagerTest.java        ← 9 tests for TransactionManager
    └── ExceptionTest.java                 ← 13 tests for all custom exceptions

docs/
└── git-workflow.md                        ← Full Git branching strategy
```

---

## Features

| Feature | Description |
|---------|-------------|
| Create Accounts | Savings or Checking, Regular or Premium customers |
| Deposit | Validates positive amounts, throws `InvalidAmountException` |
| Withdraw | Enforces min balance (Savings) / overdraft limit (Checking) |
| Transfer | Between two accounts atomically, records both sides |
| Statement | Reverse chronological history + summary totals |
| Error Handling | All errors shown as `❌ Error: <message>` in console |
| Run Tests | Simulated JUnit output in console; real tests via `mvn test` |

---

## Custom Exceptions

| Exception | When Thrown |
|-----------|-------------|
| `InvalidAmountException` | Amount is zero or negative |
| `InsufficientFundsException` | Savings withdrawal below minimum balance |
| `OverdraftExceededException` | Checking withdrawal exceeds overdraft limit |
| `InvalidAccountException` | Account number not found |

---

## Running the Application

### Prerequisites
- Java 21 (JDK)
- Apache Maven 3.8+
- IntelliJ IDEA (recommended)

### Run the application
```bash
mvn compile
mvn exec:java -Dexec.mainClass="org.example.Main"
```

Or simply open in IntelliJ and run `Main.java`.

### Run JUnit tests
```bash
mvn test
```

### Run a specific test class
```bash
mvn test -Dtest=AccountTest
mvn test -Dtest=TransactionManagerTest
mvn test -Dtest=ExceptionTest
```

---

## Console UI Preview

```
==================================================
   BANK ACCOUNT MANAGEMENT SYSTEM
==================================================
  Main Menu:
  ------------------------------
  1. Manage Accounts
  2. Perform Transactions
  3. Generate Account Statements
  4. Run Tests
  5. Exit
==================================================
Enter your choice: _
```

---

## JUnit Test Summary

| Class | Tests | Coverage |
|-------|-------|----------|
| `AccountTest` | 11 | Deposit, withdraw, overdraft, interest, types |
| `TransactionManagerTest` | 9 | Deposit, withdraw, transfer, counts, totals |
| `ExceptionTest` | 13 | All 4 exception types + ValidationUtils edge cases |

---

## Git Workflow

See [`docs/git-workflow.md`](docs/git-workflow.md) for the full branching
strategy, commit messages, merge instructions, and cherry-pick example.

### Quick Reference

```bash
# Feature branches
git checkout -b feature/refactor
git checkout -b feature/exceptions
git checkout -b feature/testing

# Run tests
mvn test

# Merge into main
git checkout main
git merge feature/testing
```

---

## Lab 2 Checklist

- [x] All custom exceptions implemented
- [x] JUnit tests created (30+ assertions across 3 test classes)
- [x] Code refactored for clean structure (methods ≤ 25 lines, JavaDoc)
- [x] Git workflow documented in `docs/git-workflow.md`
- [x] README includes Git workflow and test results
- [x] All Lab 1 features still functional
