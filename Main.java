import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static ArrayList<Account> accounts = new ArrayList<>();

    public static void main(String[] args) {
        // Seed some accounts
        accounts.add(new Account("1001", 1111, 500.0));
        accounts.add(new Account("1002", 2222, 1000.0));
        accounts.add(new Account("1234", 1234, 100.0));

        Scanner input = new Scanner(System.in);

        System.out.println("Welcome to Pillar Bank ATM");

        while (true) {
            System.out.print("Enter Account Number (or type exit): ");
            String accNo = input.next();
            if (accNo.equalsIgnoreCase("exit")) {
                System.out.println("Thank you for using Pillar Bank!");
                break;
            }

            Account current = findAccount(accNo);
            if (current == null) {
                System.out.println("Account not found. Try again.");
                continue;
            }

            // warn the user that they have only 3 attempts for the PIN
            System.out.println("You have 3 attempts to enter your PIN."); // note: inform user about attempt limit
            int attempts = 0; // note: counter for PIN attempts
            boolean authenticated = false; // note: track successful PIN entry
            while (attempts < 3) {
                System.out.print("Enter PIN: ");
                int pin = input.nextInt(); // note: read user PIN
                attempts++; // note: increment attempt count
                if (current.checkPin(pin)) { // note: check PIN against stored pin
                    authenticated = true; // note: successful authentication
                    break;
                } else {
                    int remaining = 3 - attempts; // note: calculate remaining attempts
                    if (remaining > 0) {
                        System.out.println("Incorrect PIN. Attempts remaining: " + remaining); // note: warn user
                    }
                }
            }
            if (!authenticated) {
                // note: after 3 failed attempts, program terminates as required
                System.out.println("Invalid Transaction (PROGRAM TERMINATE)");
                input.close();
                return; // note: exit program immediately
            }

            // Logged in
            System.out.println("Login successful.");
            boolean session = true;
            while (session) {
                System.out.println("======= Pillar Bank ATM =======");
                System.out.println("\n--- Menu ---");
                System.out.println("[1] Withdrawal");
                System.out.println("[2] Deposit");
                System.out.println("[3] Balance");
                System.out.println("[4] Send Money");
                System.out.println("[5] Bills Payment");
                System.out.println("[6] Mini Statement"); // note: new menu option to view last 5 transactions
                System.out.println("[7] Logout");
                System.out.print("Choose an option: ");
                int choice = input.nextInt();

                switch (choice) {
                    case 1:
                        // Withdrawal: show rule that withdrawals must be divisible by 100
                        System.out.printf("Balance: $%.2f\n", current.getBalance());
                        System.out.println("Note: Withdrawals allowed only in multiples of 100."); // note: display rule
                        System.out.print("Enter amount to withdraw: ");
                        double w = input.nextDouble();
                        // check divisibility by 100
                        if (((int)w) % 100 != 0) { // note: casting to int removes decimal part for divisibility check
                            System.out.println("Withdrawal amount must be divisible by 100. Transaction cancelled.");
                            break;
                        }
                        if (current.withdraw(w)) {
                            System.out.println("Please take your cash.");
                        } else {
                            System.out.println("Insufficient funds or invalid amount.");
                        }
                        break;
                    case 2:
                        System.out.print("Enter amount to deposit: ");
                        double dep = input.nextDouble();
                        if (dep <= 0) {
                            System.out.println("Amount must be positive.");
                        } else {
                            current.deposit(dep); // note: deposit will record transaction via Account.addTransaction
                            System.out.println("Deposit successful.");
                        }
                        break;
                    case 3:
                        // Balance check
                        System.out.printf("Current balance: $%.2f\n", current.getBalance());
                        break;
                    case 4:
                        // Send Money (transfer) - records transactions via Account methods
                        System.out.print("Enter target account number: ");
                        String targetAcc = input.next();
                        Account target = findAccount(targetAcc);
                        if (target == null) {
                            System.out.println("Target account not found.");
                            break;
                        }
                        System.out.print("Enter amount to transfer: ");
                        double tAmt = input.nextDouble();
                        if (current.transferTo(target, tAmt)) {
                            System.out.println("Transfer successful.");
                        } else {
                            System.out.println("Transfer failed (insufficient funds or invalid amount).");
                        }
                        break;
                    case 5:
                        // Bills Payment menu
                        // We show a submenu of common billers and let the user pick one,
                        // enter an account/reference number for that biller, and the amount.
                        // If the current account has enough balance, we deduct the amount
                        // and consider the bill 'paid'. This is a simple simulation.
                        boolean paying = true;
                        while (paying) {
                            System.out.println("\n--- Bills Payment ---");
                            System.out.println("[1] Meralco");
                            System.out.println("[2] Converge");
                            System.out.println("[3] PLDT");
                            System.out.println("[4] Maynilad");
                            System.out.println("[5] Back to main menu");
                            System.out.print("Choose a biller: ");
                            int billChoice = input.nextInt();

                            if (billChoice == 5) {
                                paying = false; // exit bills menu and return to main menu
                                break;
                            }

                            // Map numeric choice to a biller name string for display/logging
                            String biller;
                            switch (billChoice) {
                                case 1: biller = "Meralco"; break;
                                case 2: biller = "Converge"; break;
                                case 3: biller = "PLDT"; break;
                                case 4: biller = "Maynilad"; break;
                                default:
                                    System.out.println("Invalid biller selection.");
                                    continue; // re-show bills menu
                            }

                            // Ask user for the bill reference (like account or reference number)
                            System.out.print("Enter bill account/reference number: ");
                            String billRef = input.next();

                            // Ask user how much to pay for this bill
                            System.out.print("Enter amount to pay: ");
                            double billAmount = input.nextDouble();

                            // Validate amount > 0
                            if (billAmount <= 0) {
                                System.out.println("Amount must be positive. Try again.");
                                continue; // back to bills menu
                            }

                            // Attempt to withdraw from the current account to simulate payment
                            if (current.withdraw(billAmount)) {
                                // Payment successful: in a real system we'd record the transaction
                                // and notify the biller. Here we just print a confirmation message.
                                System.out.printf("Paid $%.2f to %s (Ref: %s).\n", billAmount, biller, billRef);
                                System.out.printf("New balance: $%.2f\n", current.getBalance());
                            } else {
                                // Withdrawal failed: insufficient funds or invalid amount
                                System.out.println("Payment failed: insufficient funds or invalid amount.");
                            }

                            // After a payment we loop back to the bills menu so user can pay another bill
                        }
                        break;
                    case 6:
                        // Mini Statement: display the last up to 5 transactions
                        System.out.println("\n--- Mini Statement (last up to 5 entries) ---");
                        java.util.List<String> stm = current.getMiniStatement(); // note: retrieve recent transactions
                        if (stm.isEmpty()) {
                            System.out.println("No recent transactions.");
                        } else {
                            for (String s : stm) {
                                System.out.println(s); // note: print each transaction line
                            }
                        }
                        System.out.println("-------------------------------------------");
                        break;
                    case 7:
                        session = false;
                        System.out.println("Logged out.");
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            }
        }

        input.close();
    }

    private static Account findAccount(String accountNumber) {
        for (Account a : accounts) {
            if (a.getAccountNumber().equals(accountNumber)) return a;
        }
        return null;
    }
}
