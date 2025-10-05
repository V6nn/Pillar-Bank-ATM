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
                int pin = (int) getValidDouble(input, "Enter PIN (3 Attempts): ");
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
                System.out.println("\n====== MENU ======");
                System.out.println("[1] Withdrawal");
                System.out.println("[2] Deposit");
                System.out.println("[3] Balance");
                System.out.println("[4] Mini Statement");
                System.out.println("[5] Pay Bills");
                System.out.println("[6] Logout");
                System.out.println("==================");
                System.out.print("Select Transaction: ");

                // Get user’s menu choice safely
                int choice = (int) getValidDouble(input, "");

                // ======= WITHDRAW =======
                if (choice == 1) {
                    double amt = getValidDouble(input, "Enter amount (Must be divisible by 100): ");
                    if (amt % 100 != 0) {
                        System.out.println("Amount must be a multiple of 100.");
                    } else if (current.withdraw(amt)) {
                        System.out.println("You have successfully withdrawn PHP" + amt);
                        System.out.println("Your new balance: PHP" + current.getBalance());
                        printReceipt("Withdrawal", amt, current);
                    } else {
                        System.out.println("Insufficient funds.");
                    }
                    if (!again(input)) { input.close(); return; }

                // ======= DEPOSIT =======
                } else if (choice == 2) {
                    double amt = getValidDouble(input, "Enter amount: ");
                    if (amt <= 0) {
                        System.out.println("Invalid amount.");
                    } else {
                        current.deposit(amt);
                        System.out.println("You have successfully deposited PHP" + amt);
                        System.out.println("Your new balance: PHP" + current.getBalance());
                        printReceipt("Deposit", amt, current);
                    }
                    if (!again(input)) { input.close(); return; }

                // ======= BALANCE =======
                } else if (choice == 3) {
                    System.out.println("Your current balance is: PHP" + current.getBalance());
                    current.addTransaction("Checked balance: PHP" + current.getBalance());
                    printReceipt("Balance Check", 0, current);
                    if (!again(input)) { input.close(); return; }

                // ======= MINI STATEMENT =======
                } else if (choice == 4) {
                    System.out.println("====== Mini Statement ======");
                    List<String> mini = current.getMiniStatement();
                    if (mini.isEmpty()) {
                        System.out.println("No recent transactions.");
                    } else {
                        for (int i = 0; i < mini.size(); i++) {
                            System.out.println(mini.get(i));
                        }
                    }
                    System.out.println("============================");
                    if (!again(input)) { input.close(); return; }

                // ======= PAY BILLS =======
                } else if (choice == 5) {
                    System.out.println("Select Bill Type:");
                    System.out.println("[1] Electricity");
                    System.out.println("[2] Water");
                    System.out.println("[3] WiFi");
                    int billChoice = (int) getValidDouble(input, "Enter your choice: ");

                    String billType = "";
                    if (billChoice == 1) billType = "Electricity";
                    else if (billChoice == 2) billType = "Water";
                    else if (billChoice == 3) billType = "WiFi";
                    else {
                        System.out.println("Invalid choice.");
                        continue;
                    }

                    double billAmt = getValidDouble(input, "Enter amount to pay for " + billType + ": ");
                    if (billAmt > 0 && current.withdraw(billAmt)) {
                        System.out.println("You have paid PHP" + billAmt + " for " + billType + ".");
                        current.addTransaction("Paid " + billType + ": -PHP" + billAmt);
                        printReceipt("Bill Payment - " + billType, billAmt, current);
                    } else {
                        System.out.println("Payment failed. Check balance or amount.");
                    }
                    if (!again(input)) { input.close(); return; }

                // ======= LOGOUT =======
                } else if (choice == 6) {
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

    // ======= SAFELY ASK FOR A NUMBER (avoids program crash) =======
    private static double getValidDouble(Scanner input, String message) {
        while (true) {
            if (!message.isEmpty()) System.out.print(message);
            if (input.hasNextDouble()) {
                return input.nextDouble();
            } else {
                System.out.println("Invalid input. Please enter a number.");
                input.next(); // clear wrong input
            }
        }
    }

    // ======= Asks if user wants another transaction =======
    private static boolean again(Scanner in) {
        System.out.print("Do you want another transaction? [Y/N]: ");
        char c = in.next().toUpperCase().charAt(0);
        if (c == 'Y') return true;
        System.out.println("For exit – program terminated.");
        return false;
    }

    // ======= Prints a simple text receipt =======
    private static void printReceipt(String type, double amt, Account acc) {
        System.out.println("");
        System.out.println("============ RECEIPT ============");
        System.out.println("Transaction: " + type);
        if (amt > 0) {
            System.out.println("Amount: PHP" + amt);
        }
        System.out.println("Remaining Balance: PHP" + acc.getBalance());
        System.out.println("=================================");
    }

    // ======= Finds an account based on account number =======
    private static Account findAccount(String num) {
        for (int i = 0; i < accounts.size(); i++) {
            Account a = accounts.get(i);
            if (a.getAccountNumber().equals(num)) {
                return a;
            }
        }
        return null;
    }
}