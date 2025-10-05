// We import tools we need
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// This is the Main class – where the program starts running
public class Main {

    // Account database (stores all the account objects)
    private static ArrayList<Account> accounts = new ArrayList<>();

    public static void main(String[] args) {
        // Add some sample accounts (account number, pin, starting balance)
        accounts.add(new Account("1001", 1111, 500.00));
        accounts.add(new Account("1002", 2222, 1000.00));
        accounts.add(new Account("1234", 3333, 100.00));

        Scanner input = new Scanner(System.in);
        System.out.println("Welcome to Pillar Bank ATM");

        // ===== MAIN ATM LOOP =====
        while (true) {
            System.out.print("Enter Account Number (or type exit): ");
            String accNo = input.next();

            if (accNo.equalsIgnoreCase("exit")) {
                System.out.println("Thank you for using Pillar Bank!");
                break;
            }

            // Try to find the entered account
            Account current = findAccount(accNo);
            if (current == null) {
                System.out.println("Account not found. Try again.");
                continue; // restart loop
            }

            // ===== PIN CHECK (3 tries) =====
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

            // ===== USER IS LOGGED IN =====
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

                // ===== SWITCH MENU =====
                switch (choice) {
                    case 1: // ===== WITHDRAW =====
                        double withdrawAmt = getValidDouble(input, "Enter amount (Must be divisible by 100): ");
                        if (withdrawAmt % 100 != 0) {
                            System.out.println("Amount must be a multiple of 100.");
                        } else if (current.withdraw(withdrawAmt)) {
                            System.out.println("You have successfully withdrawn PHP" + withdrawAmt);
                            System.out.println("Your new balance: PHP" + current.getBalance());
                            printReceipt("Withdrawal", withdrawAmt, current, "");
                        } else {
                            System.out.println("Insufficient funds.");
                        }
                        if (!again(input)) { input.close(); return; }
                        break;

                    case 2: // ===== DEPOSIT =====
                        double depositAmt = getValidDouble(input, "Enter amount: ");
                        if (depositAmt <= 0) {
                            System.out.println("Invalid amount.");
                        } else {
                            current.deposit(depositAmt);
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
                        System.out.println("Select Bill Type:");
                        System.out.println("[1] Electricity");
                        System.out.println("[2] Water");
                        System.out.println("[3] WiFi");
                        int billChoice = (int) getValidDouble(input, "Enter your choice: ");

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
                        if (billAmt > 0 && current.withdraw(billAmt)) {
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
                        session = false;
                        break;

                    default: // ===== INVALID CHOICE =====
                        System.out.println("Invalid option. Please try again.");
                        break;
                } // end switch
            } // end session loop
        } // end main loop

        input.close();
    }

    //QOL Methods
    // ===== SAFE INPUT (avoids crash on wrong number input) =====
    private static double getValidDouble(Scanner input, String message) {
        while (true) {
            if (!message.isEmpty()) System.out.print(message);
            if (input.hasNextDouble()) {
                return input.nextDouble();
            } else {
                System.out.println("Invalid input. Please enter a number.");
                input.next(); // clear invalid entry
            }
        }
    }

    // ===== ASK USER FOR ANOTHER TRANSACTION =====
    private static boolean again(Scanner in) {
        System.out.print("Do you want another transaction? [Y/N]: ");
        char c = in.next().toUpperCase().charAt(0);
        if (c == 'Y') return true;
        System.out.println("Exiting. Thank you for using Pillar Bank!");
        return false;
    }

    // ===== PRINT RECEIPT =====
    private static void printReceipt(String type, double amt, Account acc, String billType) {
        System.out.println("\n=========== RECEIPT ===========");
        System.out.println("Transaction Type: " + type);

        if (!billType.isEmpty()) {
            System.out.println("Bill Type: " + billType);
        }
        if (amt > 0) {
            System.out.println("Amount: PHP" + amt);
        }

        System.out.println("Remaining Balance: PHP" + acc.getBalance());
        System.out.println("Account Number: " + acc.getAccountNumber());
        System.out.println("Date: " + java.time.LocalDate.now());
        System.out.println("Time: " + java.time.LocalTime.now().withNano(0));
        System.out.println("Thank you for using Pillar Bank!");
        System.out.println("================================\n");
    }

    // ===== FIND ACCOUNT BY ACCOUNT NUMBER =====
    private static Account findAccount(String num) {
        for (Account a : accounts) {
            if (a.getAccountNumber().equals(num)) {
                return a;
            }
        }
        return null;
    }
}