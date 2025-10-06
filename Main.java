// Features:
//  Multiple accounts
//  Login with Account Number and PIN
//  Withdraw / Deposit / Check Balance
//  View Mini Statement (recent transactions)
//  Pay Bills (Electricity, Water, WiFi)
//  Proper input validation

import java.util.ArrayList;  // To store multiple accounts in a list
import java.util.List;       // Used for mini statement transactions
import java.util.Scanner;    // To get user input

public class Main {

    // This ArrayList holds ALL bank accounts
    // Each account is an object of the Account class
    private static ArrayList<Account> accounts = new ArrayList<>();

    public static void main(String[] args) {

        // accounts (AccountNumber, PIN, StartingBalance)
        accounts.add(new Account("1001", 1111, 500.00));
        accounts.add(new Account("1002", 2222, 1000.00));
        accounts.add(new Account("1234", 3333, 100.00));

        Scanner input = new Scanner(System.in);
        System.out.println("Welcome to Pillar Bank ATM");

        // === MAIN ATM LOOP ===
        // while loop so it keeps the program running until the user types "exit"
        while (true) {
            System.out.print("Enter Account Number (or type exit): ");
            String accNo = input.next();

            // Exit ATM logic
            if (accNo.equalsIgnoreCase("exit")) {
                System.out.println("Thank you for using Pillar Bank!");
                break; // stops the main loop
            }

            // Find user input account in array list
            Account current = findAccount(accNo);

            // If account not found, show error and restart
            if (current == null) {
                System.out.println("Account not found. Try again.");
                continue; // restart the loop
            }

            // === PIN CHECK Logic (3 attempts allowed) ===
            int tries = 0;
            boolean loggedIn = false;

            // A loop that runs up to 3 times for PIN checking
            while (tries < 3) { //tries greater than 3
                // We use getValidDouble() method to safely handle wrong input (like letters)
                int pin = (int) getValidDouble(input, "Enter PIN (3 Attempts): ");

                // checkPin() method from the Account class — compares input PIN to stored one
                if (current.checkPin(pin)) {
                    loggedIn = true;
                    break; // correct PIN entered
                } else {
                    tries++; //increment tries by 1
                    System.out.println("Wrong PIN. Attempts left: " + (3 - tries)); //if tries = 1 then (3 - 1)
                }
            }

            // If user fails all 3 attempts → exit program
            if (!loggedIn) {
                System.out.println("PROGRAM TERMINATED (Too many wrong PIN attempts)");
                input.close();
                return; // Terminates the program
            }

            // === USER IS LOGGED IN ===
            boolean session = true; // keeps the user session active
            while (session) {
                // Display the main menu
                System.out.println("\n====== MENU ======");
                System.out.println("[1] Withdrawal");
                System.out.println("[2] Deposit");
                System.out.println("[3] Balance");
                System.out.println("[4] Mini Statement");
                System.out.println("[5] Pay Bills");
                System.out.println("[6] Logout");
                System.out.println("==================");
                System.out.print("Select Transaction: ");

                //getValidDouble is the method to safe check input
                int choice = (int) getValidDouble(input, ""); 

                // === SWITCH CASE handles user’s transaction choice ===
                switch (choice) {
                    case 1: // ===== WITHDRAW =====
                        double withdrawAmt = getValidDouble(input, "Enter amount (Must be divisible by 100): ");

                        // % (modulus) checks if number is divisible by 100
                        if (withdrawAmt % 100 != 0) {
                            System.out.println("Amount must be a multiple of 100.");
                        }
                        // withdraw() returns true if successful, false if not enough balance
                        else if (current.withdraw(withdrawAmt)) { //remove Withdraw Amount from Balance
                            System.out.println("You have successfully withdrawn PHP" + withdrawAmt);
                            System.out.println("Your new balance: PHP" + current.getBalance());
                            printReceipt("Withdrawal", withdrawAmt, current, "");
                        } else {
                            System.out.println("Insufficient funds.");
                        }

                        // Ask if user wants another transaction
                        if (!again(input)) { input.close(); return; }
                        break;

                    case 2: // ===== DEPOSIT =====
                        double depositAmt = getValidDouble(input, "Enter amount: ");
                        if (depositAmt <= 0) {
                            System.out.println("Invalid amount.");
                        } else {
                            current.deposit(depositAmt); //deposit() updates balance
                            System.out.println("You have successfully deposited PHP" + depositAmt);
                            System.out.println("Your new balance: PHP" + current.getBalance());
                            printReceipt("Deposit", depositAmt, current, "");
                        }
                        if (!again(input)) { input.close(); return; }
                        break;

                    case 3: // ===== BALANCE =====
                        System.out.println("Your current balance is: PHP" + current.getBalance());
                        current.addTransaction("Checked balance: PHP" + current.getBalance());
                        printReceipt("Balance Check", 0, current, "");
                        if (!again(input)) { input.close(); return; }
                        break;

                    case 4: // ===== MINI STATEMENT =====
                        System.out.println("====== Mini Statement ======");
                        List<String> mini = current.getMiniStatement(); // Returns a list of saved transactions

                        // If no transactions recorded, show message
                        if (mini.isEmpty()) {
                            System.out.println("No recent transactions.");
                        } else {
                            // Loop through each transaction and print it
                            for (String line : mini) {
                                System.out.println(line);
                            }
                        }
                        System.out.println("============================");
                        if (!again(input)) { input.close(); return; }
                        break;

                    case 5: // ===== PAY BILLS =====
                        System.out.println("Select Bill Type:");
                        System.out.println("[1] Electricity");
                        System.out.println("[2] Water");
                        System.out.println("[3] WiFi");

                        int billChoice = (int) getValidDouble(input, "Enter your choice: ");

                        // Switch expression (modern Java feature) — assigns string based on choice
                        String billType = switch (billChoice) {
                            case 1 -> "Electricity";
                            case 2 -> "Water";
                            case 3 -> "WiFi";
                            default -> "";
                        };

                        if (billType.isEmpty()) {
                            System.out.println("Invalid bill type.");
                            break;
                        }

                        double billAmt = getValidDouble(input, "Enter amount to pay for " + billType + ": ");

                        // Withdraws or deducts money for bill if balance is enough
                        if (billAmt > 0 && current.withdraw(billAmt)) { //&& Checks if one value is false, then go to else statement
                            System.out.println("You have paid PHP" + billAmt + " for " + billType + ".");
                            current.addTransaction("Paid " + billType + ": -PHP" + billAmt);
                            printReceipt("Bill Payment", billAmt, current, billType);
                        } else {
                            System.out.println("Payment failed. Check balance or amount.");
                        }

                        if (!again(input)) { input.close(); return; }
                        break;

                    case 6: // ===== LOGOUT =====
                        System.out.println("Logged out.");
                        session = false; // Ends user session
                        break;

                    default: // ===== INVALID CHOICE =====
                        System.out.println("Invalid option. Please try again.");
                        break;
                } // end switch
            } // end session loop
        } // end main loop

        input.close(); // closes scanner 
    }

    // ==============
    // HELPER METHODS 
    // ==============

    // getValidDouble():
    // Prevents program crash if user types letters instead of numbers.
    // Loops until user enters a valid number.
    private static double getValidDouble(Scanner input, String message) {
        while (true) {
            if (!message.isEmpty()) System.out.print(message);
            if (input.hasNextDouble()) {
                return input.nextDouble(); // valid number
            } else {
                System.out.println("Invalid input. Please enter a number.");
                input.next(); // clears invalid entry
            }
        }
    }

    // again():
    // Asks user if they want to do another transaction.
    // Returns true for Y, false for N.
    private static boolean again(Scanner in) {
        System.out.print("Do you want another transaction? [Y/N]: ");
        char c = in.next().toUpperCase().charAt(0); // Parsing and start at index 0
        if (c == 'Y') return true; //If user input is Y return True otherwise return False which means Exit
        System.out.println("Exiting. Thank you for using Pillar Bank!");
        return false;
    }

    // printReceipt():
    // Prints the transaction details for the user.
    private static void printReceipt(String type, double amt, Account acc, String billType) {
        System.out.println("\n=========== RECEIPT ===========");
        System.out.println("Transaction Type: " + type);

        if (!billType.isEmpty()) { // Only prints if billType is not empty
            System.out.println("Bill Type: " + billType);
        }
        if (amt > 0) { // Shows how much money was involved in the transaction
            System.out.println("Amount: PHP" + amt);
        }

        System.out.println("Remaining Balance: PHP" + acc.getBalance()); // Show user updated balance
        System.out.println("Account Number: " + acc.getAccountNumber()); //Show Account Number
        System.out.println("Date: " + java.time.LocalDate.now()); // Show Current Date
        System.out.println("Time: " + java.time.LocalTime.now().withNano(0)); // Show Current Time WITHOUT milliseconds
        System.out.println("Thank you for using Pillar Bank!");
        System.out.println("================================\n");
    }

    // findAccount():
    // Loops through the ArrayList and finds an account that matches the entered account number.
    // Returns the Account object if found, otherwise returns null.
    private static Account findAccount(String num) {
        for (Account a : accounts) { // loop through all accounts
            if (a.getAccountNumber().equals(num)) { // check if the account number matches
                return a; // return the found account
            }
        }
        return null; // if not found, return null (no match)
    }
}