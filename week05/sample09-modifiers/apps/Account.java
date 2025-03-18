package apps;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.text.SimpleDateFormat;

public class Account {
    private static int nrOfAccounts = 0;
    public static int getNumberOfAccounts() { return nrOfAccounts; }
    private final long created;
    public Account() {
        nrOfAccounts++; // <=> Account.nrOfAccounts++;
        created = System.currentTimeMillis(); // <=> this.created = System.currentTimeMillis();
    }
    public long getCreated() { return created; }
}