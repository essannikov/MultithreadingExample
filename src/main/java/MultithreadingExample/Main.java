package MultithreadingExample;

import MultithreadingExample.part_1.ImageResizer;
import MultithreadingExample.part_2.Account;
import MultithreadingExample.part_2.Bank;
import MultithreadingExample.part_3.NodeHref;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

public class Main {
    private static int newWidth = 300;

    public static void main(String[] args) {
        System.out.println("Part value:");
        int income = (new Scanner(System.in)).nextInt();

        switch (income){
            case 1:
                //ImageResizer
                part_1();
                break;

            case 2:
                //Bank. MoneyTransfer
                part_2();
                break;

            case 31:
                //RecursiveTask
                part_3_1();
                break;

            case 32:
                //RecursiveTask
                part_3_2();
                break;

            default:
                part_default();
        }
    }

    public static void part_default(){
        //test
        /*
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<String> queue2;

        queue2 = queue;

        queue.add("qaz");
        queue.add("wsx");

        System.out.println(queue.size());
        System.out.println(queue2.size());
        */
        /*
        Semaphore semaphore = new Semaphore(2);
        try {
            semaphore.acquire();

            semaphore.release();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        */
    }

    public static void part_1(){
        String srcFolder = "/Users/essannikov/Desktop/JAVA/Lesson_11/src";
        String dstFolder = "/Users/essannikov/Desktop/JAVA/Lesson_11/dst";

        File srcDir = new File(srcFolder);
        File[] files = srcDir.listFiles();

        long start = System.currentTimeMillis();

        int cores = Runtime.getRuntime().availableProcessors(); //Processors cores count
        System.out.println(cores);

        int length = files.length;
        int middle = length / cores;
        middle = (middle < 1) ? 1 : middle;

        for (int i = 0; i < cores; i++) {
            int n = 0;

            if (i == (cores - 1)){
                n = length;
            } else {
                n = (length < middle) ? length : middle;
            }

            if (n == 0) break;

            File[] filesTmp = new File[n];
            System.arraycopy(files, (i * middle), filesTmp,0, filesTmp.length);
            ImageResizer resizer = new ImageResizer(filesTmp, newWidth, dstFolder, start);
            new Thread(resizer).start();

            length = length - middle;
        }
    }

    public static void part_2(){
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

        bank.getAccounts().forEach((s, account) ->
                System.out.println(account.getAccNumber() + " = " + account.getMoney()));
    }

    public static void part_3_1(){
        TreeMap<String, Integer> hrefContainer = new TreeMap<>();

        String url = "https://skillbox.ru";
        Integer level = 1;
        String split = "/";

        System.out.println(url);

        try {
            Document document = Jsoup.connect(url).maxBodySize(0).get();
            Elements elements = document.select("a[href]");
            for (Element element: elements){
                String text = element.attr("href");
                if (text.matches(url + ".*/")){
                    String[] textSplit = text.split(split);
                    //Take the first level
                    text = "";
                    for (int i = 0; i < textSplit.length; i++) {
                        if (i > (2 + level)){ break; }
                        if (text != ""){ text = text + split; }
                        text = text + textSplit[i];
                    }
                    text = text + split;
                    hrefContainer.put(text, level);
                }
            }

            //hrefContainer.entrySet().forEach(h -> System.out.println(h.getKey()));

            for (Map.Entry<String, Integer> href : hrefContainer.entrySet()){
                String textOut = "";
                for (int i = 0; i < href.getValue(); i++) {
                    textOut = textOut + "\t";
                }
                textOut = textOut + href.getKey();
                System.out.println(textOut);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void part_3_2(){
        TreeMap<String, Integer> hrefContainer = new TreeMap<>();

        String url = "https://skillbox.ru";
        Integer level = 0;

        hrefContainer.put(url, level);

        hrefContainer.putAll(new ForkJoinPool().invoke(new NodeHref(url, level)));

        //Save
        File newFile = new File("src/main/resources/part_3_2.txt");
        try {
            newFile.createNewFile();
            String textFull = "";

            for (Map.Entry<String, Integer> href : hrefContainer.entrySet()){
                String textOut = "";
                for (int i = 0; i < href.getValue(); i++) {
                    textOut = textOut + "\t";
                }
                textOut = textOut + href.getKey();

                textFull = textFull + textOut + System.lineSeparator();

                //Files.writeString(Paths.get(newFile.toURI()), textOut);

                System.out.println(textOut);
            }

            //Files.writeString(Paths.get(newFile.toURI()),
            //        hrefContainer.entrySet().stream().map(Object::toString).collect(Collectors.joining(System.lineSeparator())));
            Files.writeString(Paths.get(newFile.toURI()), textFull);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}