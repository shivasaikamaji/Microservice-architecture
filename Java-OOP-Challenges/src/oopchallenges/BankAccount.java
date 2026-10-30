package oopchallenges;

public class BankAccount {

    // Private variables (Encapsulation)
    private String accountHolder;
    private long accountNumber;
    private double balance;

    // Constructor
    public BankAccount(String accountHolder,
                       long accountNumber,
                       double balance) {

        this.accountHolder = accountHolder;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    // Deposit method
    public void deposit(double amount) {

        if (amount > 0) {
            balance = balance + amount;
            System.out.println("Deposit successful!");
        } else {
            System.out.println("Invalid deposit amount!");
        }
    }

    // Withdraw method
    public void withdraw(double amount) {

        if (amount <= 0) {
            System.out.println("Invalid withdrawal amount!");
        } else if (amount <= balance) {
            balance = balance - amount;
            System.out.println("Withdrawal successful!");
        } else {
            System.out.println("Insufficient balance!");
        }
    }

    // Display account details
    public void displayDetails() {

        System.out.println("Account Holder: " + accountHolder);
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Available Balance: " + balance);
    }

    // Main method
    public static void main(String[] args) {

        BankAccount account =
                new BankAccount("Shiva", 123456789, 5000);

        account.displayDetails();

        System.out.println("------------------");

        account.deposit(2000);

        account.withdraw(1500);

        System.out.println("------------------");

        account.displayDetails();
    }
}