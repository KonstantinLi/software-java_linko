import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BankTest {

    static Bank bank;
    static List<String> numbers;

    @BeforeAll
    static void setUp() {
        bank = new Bank();
        List<Account> accounts = List.of(
                new Account(150_000, "4GH4161ASD"),
                new Account(670_000, "5JAZ39NJ45"),
                new Account(1_550_500, "XE41G52G50"),
                new Account(10_250_000, "351AX5I98L"),
                new Account(560_000, "346ZXG15L1"),
                new Account(750_000, "456ZGB248Y"),
                new Account(15_400_000, "56AZV27HI2")
        );
        bank.addAll(accounts);

        numbers = accounts.stream().map(Account::getAccNumber).toList();
    }

    @Test
    @Order(1)
    void TotalBalanceAtBegin() {
        long total = bank.getTotalBalance();
        Assertions.assertEquals(29_330_500, total, () -> "Total balance failed");
    }

    @Test
    @Order(2)
    void totalBalanceAfterTransactionsWithoutFraud() {
        long amount = 10_000L;
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            threads.add(new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    int[] random = getTwoRandomIntegers();

                    String accountFromNum = numbers.get(random[0]);
                    String accountToNum = numbers.get(random[1]);

                    bank.transfer(accountFromNum, accountToNum, amount);
                }
            }));
        }

        threads.forEach(thread -> {
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        long total = bank.getTotalBalance();
        Assertions.assertEquals(29_330_500, total, () -> "Total balance failed");
    }

    @Test
    @Order(4)
    void blockAccounts() {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            final int finalI = i;
            threads.add(new Thread(() -> bank.blockAccount(numbers.get(finalI))));
        }

        startAndJoinThreads(threads);

        int blocked = bank.getCountOfBlockedAccounts();
        Assertions.assertEquals(7, blocked);
    }

    @Test
    @Order(5)
    void unblockAccounts() {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            final int finalI = i;
            threads.add(new Thread(() -> bank.unblockAccount(numbers.get(finalI))));
        }

        startAndJoinThreads(threads);

        int blocked = bank.getCountOfBlockedAccounts();
        Assertions.assertEquals(0, blocked);
    }

    @Test
    @Order(3)
    void totalBalanceAndBlockedAccountsAfterFraudTransaction() {
        long amount = 100_000L;
        AtomicBoolean isFraud = new AtomicBoolean();

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            threads.add(new Thread(() -> {
                while (!isFraud.get()) {
                    int[] random = getTwoRandomIntegers();

                    String accountFromNum = numbers.get(random[0]);
                    String accountToNum = numbers.get(random[1]);

                    boolean successfulTransfer = bank.transfer(accountFromNum, accountToNum, amount);
                    if (!successfulTransfer) {
                        isFraud.set(true);
                    }
                }
            }));
        }

        startAndJoinThreads(threads);

        int blockedAccounts = bank.getCountOfBlockedAccounts();
        long total = bank.getTotalBalance();

        Assertions.assertAll(
                () -> Assertions.assertEquals(2, blockedAccounts),
                () -> Assertions.assertEquals(29_330_500, total, () -> "Total balance failed"));
    }

    private int[] getTwoRandomIntegers() {
        int range = numbers.size();

        int first = (int)(Math.random() * range);
        int second = first;
        while (first == second) {
            second = (int)(Math.random() * range);
        }

        return new int[] {first, second};
    }

    private void startAndJoinThreads(Collection<Thread> threads) {
        threads.forEach(thread -> {
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
