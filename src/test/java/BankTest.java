import junit.framework.TestCase;
import MultithreadingExample.part_2.Account;
import MultithreadingExample.part_2.Bank;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class BankTest extends TestCase {
    public void testTransfer() {
        int amountFix = 1000000;

        Hashtable<String, Account> accounts = new Hashtable<>();
        accounts.put("1", new Account(amountFix, "1"));
        accounts.put("2", new Account(amountFix, "2"));
        accounts.put("3", new Account(amountFix, "3"));
        accounts.put("4", new Account(amountFix, "4"));
        accounts.put("5", new Account(amountFix, "5"));

        Bank bank = new Bank();
        bank.setAccounts(accounts);

        for (int i = 0; i < 1000; i++) {
            String accountFrom = String.valueOf (new Random().nextInt(4) + 1);
            String accountTo = String.valueOf (new Random().nextInt(4) + 1);

            long amount = new Random().nextInt(amountFix);

            new Thread(() -> {
                try {
                    bank.transfer(accountFrom, accountTo, amount);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        //for (Map.Entry<String, Account> entry : accounts.entrySet()){ }
        bank.getAccounts().forEach((s, account) -> {
            System.out.println(account.getAccNumber() + " = " + account.getMoney());}
        );

        long sumResult = 0;
        Iterator<Map.Entry<String, Account>> iterator = bank.getAccounts().entrySet().iterator();

        while (iterator.hasNext()){
            Map.Entry<String, Account> entry = iterator.next();
            sumResult = sumResult + entry.getValue().getMoney();
        }

        assertEquals(amountFix * 5, sumResult);
    }
}
