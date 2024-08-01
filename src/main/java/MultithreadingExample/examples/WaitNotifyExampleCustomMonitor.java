package MultithreadingExample.examples;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class WaitNotifyExampleCustomMonitor {
    AtomicInteger result = new AtomicInteger();
    Object monitor = new Object();

    public AtomicInteger getResult() {
        return result;
    }

    public void setResult(AtomicInteger result) {
        this.result = result;
    }

    public void addResult(int add) {
        this.result.addAndGet(add);
    }

    public Object getMonitor() {
        return monitor;
    }

    public void setMonitor(Object monitor) {
        this.monitor = monitor;
    }

    public static void main(String[] args) throws InterruptedException{
        ArrayList<Thread> threadArrayList = new ArrayList<>();
        WaitNotifyExampleCustomMonitor cm = new WaitNotifyExampleCustomMonitor();

        for (int i = 0; i < 3; i++) {
            int finalI = i;

            Thread thread = new Thread(() -> {
                try {
                    synchronized (cm.getMonitor()){
                        cm.getMonitor().wait();
                        cm.addResult(finalI);
                        System.out.println("Add " + finalI + " = " + cm.getResult().get());
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

            threadArrayList.add(thread);

            thread.start();
        }

        synchronized (cm.getMonitor()){
            System.out.println("Waiting...");
            Thread.sleep(5000);

            System.out.println("Notify all");
            cm.getMonitor().notifyAll();
        }

        threadArrayList.forEach(o -> {
            try {
                o.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("End");
    }
}
