public class BankAccount {
    // Static field shared among all accounts
    static int totalAccounts = 0;

    // Instance fields
    public String accountHolder;
    public double balance;

    // Constructor with 2 local variables
    public BankAccount(String holder, double initialDeposit) {
        this.accountHolder = holder;
        this.balance = initialDeposit;
        totalAccounts++;
    }

    // Method to deposit money
    public void deposit(double amount) {
        // Primitive local variable
        double previousBalance = this.balance;
        if (amount > 0) {
            this.balance += amount;
            System.out.println("Deposit amount " + amount + " to " + this.accountHolder);
            System.out.println("Previous Balance: " + previousBalance);
            System.out.println("New Balance: " + this.balance);
        }
    }

    // Main method to demonstrate usage
    public static void main(String[] args) {
        BankAccount account1;        // Type reference
        account1 = new BankAccount("Alice", 100); // Object reference
        account1.deposit(50);
        new BankAccount("Bob", 200); // Object reference (with no variable type reference)
        System.out.println("Total Accounts: " + BankAccount.totalAccounts);
    }
}