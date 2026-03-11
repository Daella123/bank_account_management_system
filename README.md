# Bank Account Management System

A comprehensive console-based banking application built with Java that demonstrates Object-Oriented Programming (OOP) principles, inheritance, polymorphism, and basic Data Structures & Algorithms (DSA) concepts.

## Project Overview

This project implements a complete bank account management system with multiple account types, customer categories, transaction processing, and comprehensive reporting features.

**Complexity:** Medium  
**Estimated Development Time:** 10 hours

## Features

### Core Functionality

1. **Account Management**
   - Create new bank accounts with customer information
   - View all accounts with comprehensive details
   - Support for multiple account types (Savings, Checking)
   - Auto-generated unique account numbers

2. **Transaction Processing**
   - Deposit money to accounts
   - Withdraw money with proper validation
   - Transaction confirmation workflow
   - Complete transaction history tracking

3. **Customer Types**
   - **Regular Customer**: Standard banking services
   - **Premium Customer**: Enhanced benefits, waived fees, minimum balance $10,000

4. **Account Types**
   - **Savings Account**: 
     - Interest rate: 3.5% annually
     - Minimum balance requirement: $500
   - **Checking Account**: 
     - Overdraft limit: $1,000
     - Monthly fee: $10 (waived for Premium customers)

5. **Transaction History**
   - View complete transaction history by account
   - Display summary statistics (total deposits, withdrawals, net change)
   - Reverse chronological order (newest first)

## Technology Stack

- **Language:** Java
- **Build Tool:** Maven
- **JDK Version:** Compatible with Java 8+
- **IDE:** IntelliJ IDEA (or any Java IDE)

## Project Structure

```
bankAccountManagement/
├── src/
│   └── main/
│       └── java/
│           └── org/
│               └── example/
│                   ├── Main.java
│                   ├── Account.java (abstract)
│                   ├── SavingsAccount.java
│                   ├── CheckingAccount.java
│                   ├── Customer.java (abstract)
│                   ├── RegularCustomer.java
│                   ├── PremiumCustomer.java
│                   ├── Transactable.java (interface)
│                   ├── Transaction.java
│                   ├── AccountManager.java
│                   └── TransactionManager.java
├── pom.xml
└── README.md
```

## Class Architecture

### 1. Customer Hierarchy

#### Customer (Abstract Class)
- **Fields:** customerId, name, age, contact, address
- **Static Field:** customerCounter (for unique ID generation)
- **Abstract Methods:** 
  - `displayCustomerDetails()`
  - `getCustomerType()`

#### RegularCustomer extends Customer
- Standard banking services
- No special privileges

#### PremiumCustomer extends Customer
- **Additional Field:** minimumBalance ($10,000)
- **Special Method:** `hasWaivedFees()` - returns true
- Enhanced benefits and priority service

### 2. Account Hierarchy

#### Account (Abstract Class)
- **Fields:** accountNumber, customer, balance, status
- **Static Field:** accountCounter (for unique ID generation)
- **Methods:** 
  - `deposit(double amount)`
  - `withdraw(double amount)`
- **Abstract Methods:**
  - `displayAccountDetails()`
  - `getAccountType()`

#### SavingsAccount extends Account
- **Fields:** interestRate (3.5%), minimumBalance ($500)
- **Override:** `withdraw()` - enforces minimum balance
- **Special Method:** `calculateInterest()`

#### CheckingAccount extends Account
- **Fields:** overdraftLimit ($1,000), monthlyFee ($10)
- **Override:** `withdraw()` - allows overdraft
- **Special Method:** `applyMonthlyFee()` - waived for Premium customers

### 3. Transaction System

#### Transactable (Interface)
- **Method:** `processTransaction(double amount, String type)`

#### Transaction (Class)
- **Fields:** transactionId, accountNumber, type, amount, balanceAfter, timestamp
- **Static Field:** transactionCounter (for unique ID generation)
- Auto-generates transaction ID and timestamp

### 4. Management Classes

#### AccountManager
- **Data Structure:** Account array (size 50)
- **Methods:**
  - `addAccount(Account)` - Add new account
  - `findAccount(String)` - Linear search to find account by ID
  - `viewAllAccounts()` - Display all accounts in tabular format
  - `getTotalBalance()` - Calculate total bank balance
  - `getAccountCount()` - Get number of accounts

#### TransactionManager
- **Data Structure:** Transaction array (size 200)
- **Methods:**
  - `addTransaction(Transaction)` - Record new transaction
  - `viewTransactionsByAccount(String)` - Display transaction history
  - `calculateTotalDeposits(String)` - Sum all deposits
  - `calculateTotalWithdrawals(String)` - Sum all withdrawals

## OOP Principles Demonstrated

1. **Encapsulation**
   - Private fields with public getters/setters
   - Protected balance modification methods

2. **Inheritance**
   - Customer hierarchy (Customer → RegularCustomer/PremiumCustomer)
   - Account hierarchy (Account → SavingsAccount/CheckingAccount)

3. **Polymorphism**
   - Method overriding (`displayAccountDetails()`, `withdraw()`)
   - Abstract classes and methods
   - Interface implementation (Transactable)

4. **Abstraction**
   - Abstract classes (Account, Customer)
   - Interface (Transactable)

5. **Composition**
   - AccountManager has Account array
   - TransactionManager has Transaction array
   - Account has Customer reference

6. **Static Members**
   - Static counters for unique ID generation
   - Shared across all instances

## Data Structures & Algorithms

1. **Arrays**
   - Account storage (fixed size: 50)
   - Transaction storage (fixed size: 200)

2. **Linear Search**
   - Finding accounts by account number: O(n)
   - Finding transactions by account number: O(n)

3. **Time Complexity Considerations**
   - Account lookup: O(n) - linear search through account array
   - Transaction history: O(n) - linear search through transaction array
   - Add operations: O(1) - append to array

## How to Run

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Maven (optional, for build management)

### Running the Application

#### Using IDE (IntelliJ IDEA, Eclipse, etc.)
1. Open the project in your IDE
2. Navigate to `src/main/java/org/example/Main.java`
3. Run the `Main` class

#### Using Command Line

1. **Compile the project:**
   ```bash
   javac -d target/classes src/main/java/org/example/*.java
   ```

2. **Run the application:**
   ```bash
   java -cp target/classes org.example.Main
   ```

#### Using Maven
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="org.example.Main"
```

## Usage Guide

### Main Menu Options

```
BANK ACCOUNT MANAGEMENT - MAIN MENU
1. Create Account
2. View Accounts
3. Process Transaction
4. View Transaction History
5. Exit
```

### Creating an Account

1. Select option `1` from main menu
2. Enter customer details (name, age, contact, address)
3. Choose customer type (Regular or Premium)
4. Choose account type (Savings or Checking)
5. Enter initial deposit amount
6. Receive account confirmation with account number

### Processing Transactions

1. Select option `3` from main menu
2. Enter account number
3. Choose transaction type (Deposit or Withdrawal)
4. Enter amount
5. Review transaction details
6. Confirm or cancel the transaction

### Viewing Transaction History

1. Select option `4` from main menu
2. Enter account number
3. View complete transaction history with summary statistics

## Sample Data

The application initializes with 5 sample accounts:

| Account | Customer      | Type     | Customer Type | Balance     |
|---------|---------------|----------|---------------|-------------|
| ACC001  | John Smith    | Savings  | Regular       | $5,250.00   |
| ACC002  | Sarah Johnson | Checking | Regular       | $3,450.00   |
| ACC003  | Michael Chen  | Savings  | Premium       | $15,750.00  |
| ACC004  | Emily Brown   | Checking | Regular       | $880.00     |
| ACC005  | David Wilson  | Savings  | Premium       | $25,200.00  |

**Total Bank Balance:** $50,530.00

## Input Validation

- **Account Numbers:** Validated against existing accounts
- **Amounts:** Must be positive numbers
- **Withdrawals:** Check sufficient balance and minimum requirements
- **Savings Withdrawals:** Ensure minimum balance ($500) is maintained
- **Checking Withdrawals:** Allow overdraft up to limit ($1,000)
- **Menu Choices:** Integer validation with error handling

## Business Rules

1. **Savings Account Withdrawals**
   - Cannot withdraw if balance after withdrawal < $500
   - Maintains minimum balance requirement

2. **Checking Account Withdrawals**
   - Can overdraw up to $1,000
   - Balance can go negative within overdraft limit

3. **Premium Customer Benefits**
   - Monthly fees are waived on checking accounts
   - Higher transaction limits (not enforced in current version)
   - Priority service designation

4. **Transaction Processing**
   - All transactions require confirmation before finalization
   - Failed transactions do not affect account balance
   - Transaction history maintained chronologically



## Learning Objectives Achieved

✅ Applied OOP principles (encapsulation, inheritance, polymorphism, abstraction)  
✅ Created well-structured applications with custom objects  
✅ Analyzed class relationships (inheritance vs composition)  
✅ Implemented proper encapsulation and naming conventions  
✅ Applied polymorphic behavior with method overriding  
✅ Implemented fundamental DSA concepts (arrays, linear search)  
✅ Demonstrated understanding of time complexity  
✅ Built a complete console application with menu navigation  
✅ Implemented input validation and error handling  

## Author

Created as a learning project to demonstrate Java OOP and DSA concepts.

## License

This project is created for educational purposes.

---

**Last Updated:** March 2026  
**Version:** 1.0.0
