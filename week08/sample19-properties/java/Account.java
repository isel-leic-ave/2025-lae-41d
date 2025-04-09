public class Account {
    private static int numberOfAccounts = 0;
    public static int getNumberOfAccounts() { return numberOfAccounts; }
    private final long created;
    public Account() {
        numberOfAccounts++; // <=> Account.numberOfAccounts++;
        created = System.currentTimeMillis(); // <=> this.created = System.currentTimeMillis();
    }
    public long getCreated() { return created; }
}