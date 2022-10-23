import java.util.*;
import java.util.stream.Collectors;

public class Bank {

    private final Map<String, Account> accounts = new Hashtable<>();
    private final Map<String, Account> blockedAccounts = new Hashtable<>();
    private final Random random = new Random();

    private boolean isFraud(String fromAccountNum, String toAccountNum, long amount)
        throws InterruptedException {
        Thread.sleep(1000);
        return random.nextBoolean();
    }

    public boolean transfer(String fromAccountNum, String toAccountNum, long amount) {
        Account fromAccount = accounts.get(fromAccountNum);
        Account toAccount = accounts.get(toAccountNum);

        Object lock1 = fromAccount;
        Object lock2 = toAccount;

        Comparator<Account> comparator = (o1, o2) -> {
          List<Account> values = new ArrayList<>(accounts.values());
          return values.indexOf(o1) - values.indexOf(o2);
        };

        if (comparator.compare(fromAccount, toAccount) < 0) {
            lock1 = toAccount;
            lock2 = fromAccount;
        }

        synchronized (lock1) {
            synchronized (lock2) {
                if (fromAccount != null && toAccount != null && amount > 0) {
                    long moneyFrom = fromAccount.getMoney();
                    if (moneyFrom < amount) {
                        throw new IllegalArgumentException("The size of transfer exceeds the current balance.");
                    }

                    fromAccount.setMoney(moneyFrom - amount);
                    toAccount.setMoney(toAccount.getMoney() + amount);

                    try {
                        if (amount > 50_000L) {
                            boolean isFraud = isFraud(fromAccountNum, toAccountNum, amount);
                            if (isFraud) {
                                blockAccount(fromAccountNum);
                                blockAccount(toAccountNum);
                                return false;
                            }
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return true;
                }
            }
        }

        return false;
    }

    public long getBalance(String accountNum) {
        Account account = accounts.get(accountNum);
        if (account == null) {
            throw new IllegalArgumentException("Account with each number doesn't exist.");
        }
        return account.getMoney();
    }

    public synchronized long getTotalBalance() {
        List<Account> accounts = new ArrayList<>(this.accounts.values());
        accounts.addAll(blockedAccounts.values());

        return accounts.stream().mapToLong(Account::getMoney).sum();
    }

    public Account getAccount(String accountNum) {
        return accounts.get(accountNum);
    }

    public int getCountOfBlockedAccounts() {
        return blockedAccounts.size();
    }

    public void add(Account account) {
        String accountNum = account.getAccNumber();
        accounts.put(accountNum, account);
    }

    public synchronized void addAll(Collection<Account> accounts) {
        Map<String, Account> map;
        map = accounts
                .stream()
                .collect(Collectors.toMap(Account::getAccNumber, account -> account));
        this.accounts.putAll(map);
    }

    public void blockAccount(String accountNum) {
        if (accounts.get(accountNum) != null) {
            blockedAccounts.put(accountNum, accounts.remove(accountNum));
        }
    }

    public void unblockAccount(String accountNum) {
        if (!blockedAccounts.containsKey(accountNum)) {
            return;
        }
        accounts.put(accountNum, blockedAccounts.remove(accountNum));
    }
}
