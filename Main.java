// We import tools we need
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// This is the Main class – where the program starts running
public class Main {

    // We make a list that stores all bank accounts
    private static ArrayList<Account> accounts = new ArrayList<>();

    // The main method – this is where the program starts
    public static void main(String[] args) {

        // Add some sample accounts (account number, pin, starting balance)
        accounts.add(new Account("1234", 1111, 500.00));
        accounts.add(new Account("1235", 2222, 1000.00));
        accounts.add(new Account("1236", 3333, 100.00));

        // Create a Scanner so we can get input from the user
        Scanner input = new Scanner(System.in);

        // Welcome message
        System.out.println("Welcome to Pillar Bank ATM");

        // Start the main loop (runs until user types "exit")
        while (true) {
            System.out.print("Enter Account Number (or type exit): ");
            String accNo = input.next();

            // If the user types "exit", the program ends
            if (accNo.equalsIgnoreCase("exit")) {
                System.out.println("Thank you for using Pillar Bank!");
                break;
            }

            // Try to find the account the user entered
            Account current = findAccount(accNo);

            // If the account does not exist, show error and restart
            if (current == null) {
                System.out.println("Account not found. Try again.");
                continue; // go back to the top of the while loop
            }

            // ======= PIN CHECK (3 tries) =======
            int tries = 0; // counts how many times user entered wrong PIN
            boolean loggedIn = false; // becomes true if user enters correct PIN

            // Loop until correct PIN or 3 wrong tries
            while (tries < 3) {
                System.out.print("Enter PIN (3 Attempts): ");
                int pin = input.nextInt();

                // Check if PIN is correct
                if (current.checkPin(pin)) {
                    loggedIn = true;
                    break; // stop asking for PIN
                } else {
                    tries++; // add one wrong attempt
                    System.out.println("Wrong PIN. Attempts left: " + (3 - tries));
                }
            }

            // If 3 wrong tries, end the program
            if (!loggedIn) {
                System.out.println("PROGRAM TERMINATED (Too many wrong PIN attempts)");
                input.close();
                return; // exit the program
            }

            // ======= USER IS NOW LOGGED IN =======
            boolean session = true;

            // Loop while the user is logged in
            while (session) {
                // Show menu options
                System.out.println("====== MENU ======");
                System.out.println("[1] Withdrawal");
                System.out.println("[2] Deposit");
                System.out.println("[3] Balance");
                System.out.println("[4] Mini Statement");
                System.out.println("[5] Logout");
                System.out.println("==================");
                System.out.print("Select Transaction: ");

                // Get user’s menu choice
                int choice = input.nextInt();

                // ======= WITHDRAW =======
                if (choice == 1) {
                    System.out.print("Enter amount (Must be divisibles by 100): ");
                    double amt = input.nextDouble();

                    // Check if amount is valid (must be multiple of 100)
                    if (amt % 100 != 0) {
                        System.out.println("Amount must be a multiple of 100.");
                    } else if (current.withdraw(amt)) {
                        System.out.println("You have successfully withdrawn PHP" + amt);
                        System.out.println("Your new balance: PHP" + current.getBalance());
                        printReceipt("Withdrawal", amt, current);
                    } else {
                        System.out.println("Insufficient funds.");
                    }

                    if (!again(input)) {
                        input.close();
                        return;
                    }

                // ======= DEPOSIT =======
                } else if (choice == 2) {
                    System.out.print("Enter amount: ");
                    double amt = input.nextDouble();

                    if (amt <= 0) {
                        System.out.println("Invalid amount.");
                    } else {
                        current.deposit(amt);
                        System.out.println("You have successfully deposited PHP" + amt);
                        System.out.println("Your new balance: PHP" + current.getBalance());
                        printReceipt("Deposit", amt, current);
                    }

                    if (!again(input)) {
                        input.close();
                        return;
                    }

                // ======= BALANCE =======
                } else if (choice == 3) {
                    System.out.println("Your current balance is: PHP" + current.getBalance());
                    current.addTransaction("Checked balance: PHP" + current.getBalance());
                    printReceipt("Balance Check", 0, current);

                    if (!again(input)) {
                        input.close();
                        return;
                    }

                // ======= MINI STATEMENT =======
                } else if (choice == 4) {
                    System.out.println("====== Mini Statement ======");
                    List<String> mini = current.getMiniStatement();

                    if (mini.isEmpty()) {
                        System.out.println("No recent transactions.");
                    } else {
                        // Simpler for loop
                        for (int i = 0; i < mini.size(); i++) {
                            String t = mini.get(i);
                            System.out.println(" - " + t);
                        }
                    }

                    System.out.println("============================");

                    if (!again(input)) {
                        input.close();
                        return;
                    }

                // ======= LOGOUT =======
                } else if (choice == 5) {
                    System.out.println("Logged out.");
                    session = false;

                // ======= INVALID CHOICE =======
                } else {
                    System.out.println("Invalid option.");
                }
            } // end of session loop
        } // end of main loop

        input.close();
    }

    // ======= Helper Methods Below =======
    // This method asks the user if they want to do another transaction
    private static boolean again(Scanner in) {
        // Ask the user a question (Y for Yes, N for No)
        System.out.print("Do you want another transaction? [Y/N]: ");

        // Read the user's answer:
        // in.next() → reads what the user typed (like "Y" or "n")
        // .toUpperCase() → turns it into uppercase (so "n" becomes "N")
        // .charAt(0) → gets the first letter only
        char c = in.next().toUpperCase().charAt(0);

        // If the user typed Y, return true (means: yes, continue)
        if (c == 'Y') return true;

        // If the user typed anything else, print message and stop
        System.out.println("For exit – program terminated.");

        // Return false (means: no, stop the program)
        return false;
    }

    // This method prints a simple receipt after every transaction
    private static void printReceipt(String type, double amt, Account acc) {
        System.out.println(""); // print an empty line for spacing
        System.out.println("============ RECEIPT ============"); // header
        System.out.println("Transaction: " + type); // show the type (Deposit, Withdraw, etc.)

        // Only show amount if greater than 0
        if (amt > 0) {
            System.out.println("Amount: PHP" + amt);
        }

        // Show the remaining balance after the transaction
        System.out.println("Remaining Balance: PHP" + acc.getBalance());
        System.out.println("================================="); // end of receipt
    }

    // This method looks for an account using the account number
    private static Account findAccount(String num) {
        // Loop through all accounts in the list
        for (int i = 0; i < accounts.size(); i++) {
            Account a = accounts.get(i); // get one account at a time

            // If the account number matches what the user typed, return it
            if (a.getAccountNumber().equals(num)) {
                return a; // found it!
            }
        }

        // If no account matches, return null (means "not found")
        return null;
    }
}