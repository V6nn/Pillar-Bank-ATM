// ArrayList and List for storing transactions
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

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
        if (amount > 0) {
            balance += amount; // Add the deposit amount to the balance
            addTransaction("Deposit: +PHP" + amount); // Record with + sign
        }
    }

    // Withdraw from Account
    // Returns true if successful, false if not enough balance
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            addTransaction("Withdrawal: -PHP" + amount); // Record with - sign
            return true;
        }
        return false;
    }

    // New method: For bill payments (uses -PHP format, no "Withdrawal" label)
    public boolean payBill(String billType, double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            addTransaction("Paid " + billType + ": -PHP" + amount); // Clear and formatted
            return true;
        }
        return false;
    }

    // Adds a line to the transaction list (with timestamp)
    public void addTransaction(String info) {
        // Add date and time for better record accuracy
        String timestamp = LocalDate.now() + " " + LocalTime.now().withNano(0);
        transactions.add(info + " | " + timestamp);

        // Keep only the last 5 transactions
        if (transactions.size() > 5) {
            transactions.remove(0);
        }
    }

    // Returns the list of transactions (for the mini statement)
    public List<String> getMiniStatement() {
        return transactions;
    }
}