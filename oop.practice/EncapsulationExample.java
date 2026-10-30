
public class EncapsulationExample {

    static class BankAccount {
        private double balance;

        public BankAccount(double initialBalance) {
            if (initialBalance < 0) {
                throw new IllegalArgumentException(
                    "Balance cannot be negative"
                );
            }
            this.balance = initialBalance;
        }

        public void deposit(double amount) {
            if (amount > 0) {
                balance += amount;
                System.out.println("Deposit successful");
            } else {
                System.out.println("Invalid deposit amount");
            }
        }

        public double getBalance() {
            return balance;
        }
    }

    public static void main(String[] args) {
        BankAccount account = new BankAccount(1000);

        account.deposit(500);

        System.out.println("Current balance: " + account.getBalance());
    }
}
