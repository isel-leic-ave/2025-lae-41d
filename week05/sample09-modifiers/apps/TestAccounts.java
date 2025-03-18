package apps;

class TestAccounts {
    public static void main(String[] args) throws InterruptedException {
        Account a1 = new Account();
        Thread.sleep(10); // Sleep for 10ms
        Account a2 = new Account();
        System.out.println(a1.getNumberOfAccounts());
        System.out.println(a2.getNumberOfAccounts());
        System.out.println(Account.getNumberOfAccounts());
        System.out.println(a1.getCreated());
        System.out.println(a2.getCreated());
    }
}