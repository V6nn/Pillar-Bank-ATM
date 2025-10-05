public class Account {
    // Unique identifier for this account (could be numeric or alphanumeric)
    private String accountNumber;

    // Simple integer PIN used for authentication in this demo app
    private int pin;

    // Account balance stored as a double for simplicity in this school project.
    // Note: for real money handling use BigDecimal to avoid precision errors.
    private double balance;
    // list to hold recent transaction descriptions for the mini statement
    private java.util.ArrayList<String> transactions = new java.util.ArrayList<>(); // note: stores newest last

    // Constructor: initialize account with account number, pin, and starting balance
    public Account(String accountNumber, int pin, double balance) {
        this.accountNumber = accountNumber; // store id
        this.pin = pin; // store pin
        this.balance = balance; // initial balance
    }

    // Getter for account number
    public String getAccountNumber() {
        return accountNumber;
    }

    // Getter for balance. No setter is provided to enforce balance changes go
    // through deposit/withdraw/transfer methods which contain validation.
    public double getBalance() {
        return balance;
    }

    // Check whether the provided PIN matches this account's PIN.
    // Returns true for a match, false otherwise. Simple equality check.
    public boolean checkPin(int inputPin) {
        return this.pin == inputPin;
    }

    // Deposit: only accept positive amounts. If amount > 0, add to balance.
    // This method returns void because deposit on invalid input is a no-op.
    public void deposit(double amount) {
        if (amount > 0) { // note: only accept positive deposits
            this.balance += amount; // note: increase account balance
            addTransaction(String.format("Deposit: +$%.2f", amount)); // note: record transaction
        }
    }

    // Withdraw: only allow positive amounts up to the available balance.
    // If successful, deduct from balance and return true; otherwise return false.
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= this.balance) { // note: validate amount
            this.balance -= amount; // note: deduct from balance
            addTransaction(String.format("Withdrawal: -$%.2f", amount)); // note: record transaction
            return true; // note: success
        }
        return false; // note: failure (invalid amount or insufficient funds)
    }

    // Transfer funds from this account to another Account instance.
    // Returns true on success, false on failure. Conditions checked:
    // - target is not null
    // - amount is positive
    // - this account has enough balance
    // If all pass, deduct amount here and credit target by calling deposit.
    public boolean transferTo(Account target, double amount) {
        if (target == null) return false; // note: cannot transfer to null
        if (amount > 0 && amount <= this.balance) { // note: validate amount and funds
            this.balance -= amount; // note: deduct from sender
            addTransaction(String.format("Transfer Out: -$%.2f to %s", amount, target.getAccountNumber())); // note: record outgoing transfer
            // credit the target account using deposit (which also records the incoming transaction)
            target.deposit(amount); // note: this will record a Deposit transaction on the target
            target.addTransaction(String.format("Transfer In: +$%.2f from %s", amount, this.getAccountNumber())); // note: explicit incoming transaction (optional duplicate)
            return true; // note: success
        }
        return false; // note: failure
    }

    // add a transaction description to the list and trim to last 5 entries
    public void addTransaction(String desc) {
        transactions.add(desc); // note: append newest at end
        // if we have more than 5 entries, remove the oldest (index 0)
        if (transactions.size() > 5) {
            transactions.remove(0); // note: keep only last 5
        }
    }

    // return a copy of recent transactions (last up to 5 entries)
    public java.util.List<String> getMiniStatement() {
        return new java.util.ArrayList<>(transactions); // note: return defensive copy
    }
}
