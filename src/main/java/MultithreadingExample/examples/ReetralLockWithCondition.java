package MultithreadingExample.examples;

import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReetralLockWithCondition {
    volatile Stack<String> stack = new Stack<>();
    int CAPACITY = 5;

    ReentrantLock lock = new ReentrantLock();
    Condition stackEmptyCondition = lock.newCondition();
    Condition stackFullCondition = lock.newCondition();

    AtomicBoolean checkEnd = new AtomicBoolean(false);

    public Stack<String> getStack() {
        return stack;
    }

    public AtomicBoolean getCheckEnd() {
        return checkEnd;
    }

    public void setCheckEnd(boolean checkEnd) {
        this.checkEnd.set(checkEnd);
    }

    public static void main(String[] args) throws InterruptedException{
        ReetralLockWithCondition rl = new ReetralLockWithCondition();

        Thread threadSet = new Thread(() -> {
            for (int i = 0; i < 15; i++) {
                rl.pushToStack(String.valueOf(i));
            }
            rl.setCheckEnd(true);
        });

        Thread threadGet = new Thread(() -> {
            while (rl.getCheckEnd().get() == false ||
                   rl.getStack().size() > 0){
                rl.popFromStack();
            }
        });

        threadSet.start();
        threadGet.start();
        threadSet.join();
        threadGet.join();

        System.out.println("end");
    }

    public void pushToStack(String item){
        try{
            lock.lock();
            while (stack.size() == CAPACITY){
                System.out.println("Full condition waiting...");
                stackFullCondition.await();
            }
            System.out.println("Push " + item);
            stack.push(item);
            System.out.println("Empty condition signal");
            stackEmptyCondition.signalAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public String popFromStack(){
        try {
            lock.lock();
            while (stack.size() == 0) {
                System.out.println("Empty condition waiting...");
                stackEmptyCondition.await();
            }
            System.out.println("Pop element");
            return stack.pop();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("Full condition signal");
            stackFullCondition.signalAll();
            lock.unlock();
        }
    }
}
