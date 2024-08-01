package MultithreadingExample.part_2;

import java.util.Formatter;
import java.util.Hashtable;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Bank
{
    private static long amountCheck = 50000;
    private Hashtable<String, Account> accounts;

    private ConcurrentHashMap.KeySetView<Object, Boolean> accountsLock;

    private final Random random = new Random();

    public Bank() {
        this.accounts = new Hashtable<>();
        this.accountsLock = ConcurrentHashMap.newKeySet();
    }

    public synchronized boolean isFraud(String fromAccountNum, String toAccountNum, long amount)
            throws InterruptedException
    {
        Thread.sleep(1000);
        return random.nextBoolean();
    }

    /**
     * TODO: реализовать метод. Метод переводит деньги между счетами.
     * Если сумма транзакции > 50000, то после совершения транзакции,
     * она отправляется на проверку Службе Безопасности – вызывается
     * метод isFraud. Если возвращается true, то делается блокировка
     * счетов (как – на ваше усмотрение)
     */
    public void transfer(String fromAccountNum, String toAccountNum, long amount) throws Exception
    {
        synchronized (accounts.get(fromAccountNum)){
            checkAccount(fromAccountNum);
            checkBalance(fromAccountNum, amount);

            long balanceFrom = getBalance(fromAccountNum);
            accounts.get(fromAccountNum).setMoney(balanceFrom - amount);
        }

        synchronized (accounts.get(toAccountNum)){
            checkAccount(toAccountNum);

            long balanceTo = getBalance(toAccountNum);
            accounts.get(toAccountNum).setMoney(balanceTo + amount);
        }

        if (amount >= amountCheck){
            if (isFraud(fromAccountNum, toAccountNum, amount)){
                accountsLock.add(fromAccountNum);
                accountsLock.add(toAccountNum);
            }
        }
    }

    /**
     * TODO: реализовать метод. Возвращает остаток на счёте.
     */
    public long getBalance(String accountNum)
    {
        return accounts.get(accountNum).getMoney();
    }

    protected void checkAccount(String accountNum) throws Exception{
        if (accounts.get(accountNum) == null){
            Formatter f = new Formatter();
            f.format("Error. %s is not found.", accountNum);
            //throw new Exception("Error. " + accountNum + " is not found.");
            throw new Exception(String.valueOf(f));
        } else{
            checkAccountLock(accountNum);
        }
    }

    protected void checkAccountLock(String accountNum) throws Exception{
        if (accountsLock.contains(accountNum)){
            Formatter f = new Formatter();
            f.format("Error. Account %s is lock.", accountNum);
            //throw new Exception("Error. Account " + accountNum + " is lock.");
            throw new Exception(String.valueOf(f));
        }
    }

    protected void checkBalance(String fromAccountNum, long amount)throws Exception{
        if (getBalance(fromAccountNum) < amount){
            Formatter f = new Formatter();
            f.format("Error. Insufficient funds on account %s.", fromAccountNum);
            //throw new Exception("Error. Insufficient funds on account " + fromAccountNum + ".");
            throw new Exception(String.valueOf(f));
        }
    }

    public Hashtable<String, Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(Hashtable<String, Account> accounts) {
        this.accounts = accounts;
    }
}