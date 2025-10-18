import java.util.ArrayList;  // To store multiple accounts in a list
import java.util.List;       // Used for mini statement transactions
import java.util.Scanner;    // To get user input

public class Main {

    // This ArrayList holds ALL bank accounts
    // Each account is an object of the Account class
    private static ArrayList<Account> accounts = new ArrayList<>();

    public static void main(String[] args) {

        // account data base 
        // format:(AccountNumber, PIN, StartingBalance)
        accounts.add(new Account("1001", 1111, 500.00));
        accounts.add(new Account("1002", 2222, 1000.00));
        accounts.add(new Account("1234", 3333, 100.00));

        Scanner input = new Scanner(System.in);
        System.out.println("Welcome to Pillar Bank ATM");

        // === MAIN ATM LOOP ===
        while (true) {
            System.out.print("Enter Account Number (or type exit): ");
            String accNo = input.next();

            // Exit ATM logic
            if (accNo.equalsIgnoreCase("exit")) {
                System.out.println("Thank you for using Pillar Bank!");
                break;
            }

            // Find user input account in array list
            Account current = findAccount(accNo);

            // If account not found, show error and restart
            if (current == null) {
                System.out.println("Account not found. Try again.");
                continue;
            }

            // === PIN CHECK Logic (3 attempts allowed) ===
            int tries = 0;
            boolean loggedIn = false;

            while (tries < 3) {
                int pin = (int) getValidDouble(input, "Enter PIN (3 Attempts): ");
                if (current.checkPin(pin)) {
                    loggedIn = true;
                    break;
                } else {
                    tries++;
                    System.out.println("Wrong PIN. Attempts left: " + (3 - tries));
                }
            }

            if (!loggedIn) {
                System.out.println("PROGRAM TERMINATED (Too many wrong PIN attempts)");
                input.close();
                return;
            }

            // === USER IS LOGGED IN ===
            boolean session = true;
            while (session) {
                System.out.println("\n====== MENU ======");
                System.out.println("[1] Withdrawal");
                System.out.println("[2] Deposit");
                System.out.println("[3] Balance");
                System.out.println("[4] Mini Statement");
                System.out.println("[5] Pay Bills");
                System.out.println("[6] Logout");
                System.out.println("==================");
                System.out.print("Select Transaction: ");

                int choice = (int) getValidDouble(input, "");
                input.nextLine(); 

                switch (choice) {
                    case 1: // ===== WITHDRAW =====
                        double withdrawAmt = getValidDouble(input, "Enter amount (Must be divisible by 100): ");
                        input.nextLine(); 

                        if (withdrawAmt % 100 != 0) {
                            System.out.println("Amount must be a multiple of 100.");
                        } else if (current.withdraw(withdrawAmt)) {
                            System.out.println("You have successfully withdrawn PHP" + withdrawAmt);
                            System.out.println("Your new balance: PHP" + current.getBalance());
                            printReceipt("Withdrawal", -withdrawAmt, current, "");
                        } else {
                            System.out.println("Insufficient funds.");
                        }

                        if (!again(input)) { input.close(); return; }
                        break;

                    case 2: // ===== DEPOSIT =====
                        double depositAmt = getValidDouble(input, "Enter amount: ");
                        input.nextLine(); 

                        if (depositAmt <= 0) {
                            System.out.println("Invalid amount.");
                        } else {
                            current.deposit(depositAmt);
                            System.out.println("You have successfully deposited PHP" + depositAmt);
                            System.out.println("Your new balance: PHP" + current.getBalance());
                            printReceipt("Deposit", +depositAmt, current, "");
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
                        List<String> mini = current.getMiniStatement();

                        if (mini.isEmpty()) {
                            System.out.println("No recent transactions.");
                        } else {
                            for (String line : mini) {
                                System.out.println(line);
                            }
                        }
                        System.out.println("============================");
                        if (!again(input)) { input.close(); return; }
                        break;

                    case 5: // ===== PAY BILLS =====
                        while (true) {
                            System.out.println("Select Bill Type:");
                            System.out.println("[1] Electricity");
                            System.out.println("[2] Water");
                            System.out.println("[3] WiFi");
                            System.out.println("[4] Back to Menu");

                            int billChoice = (int) getValidDouble(input, "Enter your choice: ");
                            input.nextLine(); 

                            if (billChoice == 4) break; // Back to menu

                            String billType = switch (billChoice) {
                                case 1 -> "Electricity";
                                case 2 -> "Water";
                                case 3 -> "WiFi";
                                default -> "";
                            };

                            if (billType.isEmpty()) {
                                System.out.println("Invalid bill type.");
                                continue;
                            }

                            String confirmAcc = getValidString(input, "Enter Account Number to confirm payment: ");
                            if (!confirmAcc.equals(current.getAccountNumber())) {
                                System.out.println("Account number mismatch. Cancelling transaction.");
                                continue;
                            }

                            double billAmt = getValidDouble(input, "Enter amount to pay for " + billType + ": ");
                            input.nextLine(); 

                            if (current.payBill(billType, billAmt)) {
                                System.out.println("You have paid PHP" + billAmt + " for " + billType + ".");
                                printReceipt("Bill Payment", -billAmt, current, billType);
                            } else {
                                System.out.println("Payment failed. Check balance or amount.");
                            }

                            if (!again(input)) { input.close(); return; }
                        }
                        break;

                    case 6: // ===== LOGOUT =====
                        System.out.println("Logged out.");
                        session = false;
                        break;

                    default:
                        System.out.println("Invalid option. Please try again.");
                        break;
                }
            }
        }

        input.close();
    }

    // =====================
    // HELPER METHODS
    // =====================

    // getValidDouble(): ensures user inputs valid numbers
    private static double getValidDouble(Scanner input, String message) {
        while (true) {
            if (!message.isEmpty()) System.out.print(message);
            if (input.hasNextDouble()) {
                return input.nextDouble();
            } else {
                System.out.println("Invalid input. Please enter a number.");
                input.next();
            }
        }
    }

    // getValidString(): prevents blank or space-only input
    private static String getValidString(Scanner input, String message) {
        while (true) {
            System.out.print(message);
            String line = input.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("Error: Input cannot be blank or space.");
        }
    }

    // again(): ask if user wants another transaction
    private static boolean again(Scanner in) {
        System.out.print("Do you want another transaction? [Y/N]: ");
        char c = in.next().toUpperCase().charAt(0);
        in.nextLine(); 
        if (c == 'Y') return true;
        System.out.println("Exiting. Thank you for using Pillar Bank!");
        return false;
    }

    // printReceipt(): prints receipt info
    private static void printReceipt(String type, double amt, Account acc, String billType) {
        System.out.println("\n=========== RECEIPT ===========");
        System.out.println("Transaction Type: " + type);
        if (!billType.isEmpty()) {
            System.out.println("Bill Type: " + billType);
        }
        if (amt != 0) {
            System.out.println("Amount: " + (amt > 0 ? "+" : "") + "PHP" + amt);
        }
        System.out.println("Remaining Balance: PHP" + acc.getBalance());
        System.out.println("Account Number: " + acc.getAccountNumber());
        System.out.println("Date: " + java.time.LocalDate.now());
        System.out.println("Time: " + java.time.LocalTime.now().withNano(0));
        System.out.println("Thank you for using Pillar Bank!");
        System.out.println("================================\n");
    }

    // findAccount(): search account in list
    private static Account findAccount(String num) {
        for (Account a : accounts) {
            if (a.getAccountNumber().equals(num)) {
                return a;
            }
        }
        return null;
    }
}