// ArrayList and List for storing transactions
import java.util.ArrayList;
import java.util.List;

// Account class – it represents one bank account
public class Account {

    // Variables that store the account's details
    private String accountNumber;  // example: "1001"
    private int pin;               // example: 1234
    private double balance;        // example: 500.0
    private List<String> transactions; // keeps track of recent transactions

    // Constructor
    // Account Format ("Account Number", PIN , balance)
    public Account(String accountNumber, int pin, double balance) {
        this.accountNumber = accountNumber; // store the account number
        this.pin = pin;                     // store the account pin
        this.balance = balance;             // store the starting balance
        this.transactions = new ArrayList<String>(); // make an empty list for transactions
    }

    // Checks if the PIN entered is correct
    // It returns true if the inputPin matches the real pin, otherwise false
    public boolean checkPin(int inputPin) {
        return pin == inputPin;
    }

    // Show (return) the account number of this account
    public String getAccountNumber() {
        return accountNumber;
    }

    // Show (return) the current balance of this account
    public double getBalance() {
        return balance;
    }

    // Adds money to the account
    public void deposit(double amount) {
        // Only allow positive numbers
        if (amount > 0) {
            // Add the deposit amount to the balance
            balance += amount;
            // Record this transaction for the mini statement
            addTransaction("Deposited: PHP" + amount);
        }
    }

    // Withdraw from Account
    // It returns true if successful, false if not enough balance
    public boolean withdraw(double amount) {
        // Only allow positive numbers and make sure there’s enough money
        if (amount > 0 && amount <= balance) { // && checks if input is less than or equal to current balance
            // Subtract the amount from balance
            balance -= amount;
            // Record this transaction
            addTransaction("Withdrawn: PHP" + amount);
            // Withdrawal worked
            return true;
        }
        // If the amount is invalid or balance is not enough
        return false;
    }

    // This adds a line to the transaction list
    // It also removes the oldest one if there are more than 5
    public void addTransaction(String info) {
        // Add the new transaction at the end
        transactions.add(info);

        // Keep only the last 5 transactions
        if (transactions.size() > 5) {
            // Remove the oldest (first) transaction
            transactions.remove(0);
        }
    }

    // Returns the list of transactions (for the mini statement)
    public List<String> getMiniStatement() {
        return transactions;
    }
}