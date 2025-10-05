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
        accounts.add(new Account("1001", 1111, 500.0));
        accounts.add(new Account("1002", 2222, 1000.0));
        accounts.add(new Account("1234", 1234, 100.0));

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
                System.out.println("[1] Withdraw");
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
                    System.out.print("Enter amount: ");
                    double amt = input.nextDouble();

                    // Check if amount is valid (must be multiple of 100)
                    if (amt % 100 != 0) {
                        System.out.println("Amount must be a multiple of 100.");
                    } else if (current.withdraw(amt)) {
                        // If withdrawal is successful
                        System.out.println("You have successfully withdrawn ₱" + amt);
                        System.out.println("Your new balance: ₱" + current.getBalance());
                        printReceipt("Withdraw", amt, current);
                    } else {
                        System.out.println("Insufficient funds.");
                    }

                    // Ask if user wants to do another transaction
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
                        System.out.println("You have successfully deposited ₱" + amt);
                        System.out.println("Your new balance: ₱" + current.getBalance());
                        printReceipt("Deposit", amt, current);
                    }

                    if (!again(input)) {
                        input.close();
                        return;
                    }

                // ======= BALANCE =======
                } else if (choice == 3) {
                    System.out.println("Your current balance is: ₱" + current.getBalance());
                    current.addTransaction("Checked balance: ₱" + current.getBalance());
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
                        for (String t : mini) {
                            System.out.println(t);
                        }
                    }

                    System.out.println("============================");

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

        input.close(); // close the scanner when done
    }

    // ======= Helper Methods Below =======

    // This method asks the user if they want another transaction
    private static boolean again(Scanner in) {
        System.out.print("Do you want another transaction? [Y/N]: ");
        char c = in.next().toUpperCase().charAt(0); // read the first letter of input
        if (c == 'Y') return true; // if yes, return true
        System.out.println("For exit – program terminated.");
        return false; // otherwise return false
    }

    // This prints a simple text receipt
    private static void printReceipt(String type, double amt, Account acc) {
        System.out.println("=== RECEIPT ===");
        System.out.println("Transaction: " + type);
        if (amt > 0) {
            System.out.println("Amount: ₱" + amt);
        }
        System.out.println("Balance: ₱" + acc.getBalance());
        System.out.println("Recent Transactions:");
        for (String t : acc.getMiniStatement()) {
            System.out.println(" - " + t);
        }
        System.out.println("===============");
    }

    // This finds an account based on account number
    private static Account findAccount(String num) {
        for (Account a : accounts) {
            if (a.getAccountNumber().equals(num)) {
                return a; // return the account if found
            }
        }
        return null; // return null if not found
    }
}