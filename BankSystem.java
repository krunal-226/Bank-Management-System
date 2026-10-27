import java.util.HashMap;
import java.util.Map;

public class BankSystem {
    private Map<String, Account> accounts = new HashMap<>();

    public void addAccount(Account account) {
        if (account != null) {
            accounts.put(account.getAccountNumber(), account);
        }
    }

    public Account login(String accNum, String pin) throws Exception {
        if (!accounts.containsKey(accNum)) {
            throw new Exception("Account number not found!");
        }
        Account acc = accounts.get(accNum);
        if (!acc.validatePin(pin)) {
            throw new Exception("Incorrect PIN!");
        }
        return acc;
    }

    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }
}
