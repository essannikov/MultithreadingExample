package MultithreadingExample.examples;

import java.util.LinkedList;
import java.util.Random;

public class ProducerConsumerCustomSync {
    LinkedList<Integer> queue = new LinkedList<>();
    static final int LIMIT = 10;
    Object lock = new Object();

    public static void main(String[] args) throws InterruptedException{
        ProducerConsumerCustomSync pc = new ProducerConsumerCustomSync();

        Thread producerThread = new Thread(() -> {
            try {
                pc.produce();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread consumerThread = new Thread(() -> {
            try {
                pc.consume();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        producerThread.start();
        consumerThread.start();
        producerThread.join();
        consumerThread.join();

        System.out.println("end");
    }

    public void produce() throws InterruptedException{
        int i = 0;
        while (i < 1000){
            synchronized (lock){
                while (queue.size() == LIMIT){
                    System.out.println("queue is full. wait for consumer to pop...");
                    lock.wait();
                }

                queue.add(i++);
                if (i%100 == 0){
                    Thread.sleep(new Random().nextInt(200));
                }
                lock.notify();
            }
        }
    }

    public void consume() throws InterruptedException{
        while (true){
            synchronized (lock){
                while (queue.size() == 0){
                    System.out.println("queue is empty. wait for producer to add items...");
                    lock.wait();
                }

                Integer pop = queue.removeFirst();
                lock.notify();
                System.out.printf("Taken %d from queue\n", pop);
                if (pop == 999){
                    System.out.println("poison pill received !");
                    return;
                }
            }
        }
    }
}
