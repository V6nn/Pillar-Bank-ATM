// We import tools to use ArrayList and List for storing transactions
import java.util.ArrayList;
import java.util.List;

// This is the Account class – it represents one bank account
public class Account {

    // These are variables that store the account's details
    private String accountNumber;  // example: "1001"
    private int pin;               // example: 1234
    private double balance;        // example: 500.0
    private List<String> transactions; // keeps track of recent transactions

    // This is the constructor – it runs when we create a new Account object
    // Example of use: new Account("1001", 1234, 500.0);
    public Account(String accountNumber, int pin, double balance) {
        this.accountNumber = accountNumber; // store the account number
        this.pin = pin;                     // store the account pin
        this.balance = balance;             // store the starting balance
        this.transactions = new ArrayList<String>(); // make an empty list for transactions
    }

    // This checks if the PIN entered by the user is correct
    // It returns true if the inputPin matches the real pin, otherwise false
    public boolean checkPin(int inputPin) {
        return pin == inputPin;
    }

    // This gives (returns) the account number of this account
    public String getAccountNumber() {
        return accountNumber;
    }

    // This gives (returns) the current balance of this account
    public double getBalance() {
        return balance;
    }

    // This adds money to the account
    public void deposit(double amount) {
        // Only allow positive numbers
        if (amount > 0) {
            // Add the amount to the balance
            balance += amount;
            // Record this transaction for the mini statement
            addTransaction("Deposited: ₱" + amount);
        }
    }

    // This takes money from the account (withdraw)
    // It returns true if successful, false if not enough balance
    public boolean withdraw(double amount) {
        // Only allow positive numbers and make sure there’s enough money
        if (amount > 0 && amount <= balance) {
            // Subtract the amount from balance
            balance -= amount;
            // Record this transaction
            addTransaction("Withdrawn: ₱" + amount);
            // Tell the program that the withdrawal worked
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

    // This returns the list of transactions (for the mini statement)
    public List<String> getMiniStatement() {
        return transactions;
    }
}