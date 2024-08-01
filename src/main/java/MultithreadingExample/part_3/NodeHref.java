package MultithreadingExample.part_3;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.RecursiveTask;

public class NodeHref extends RecursiveTask<TreeMap<String,Integer>> {
    private String url;
    private Integer level;

    public NodeHref(String url, Integer level) {
        this.url = url;
        this.level = level;
    }

    @Override
    protected TreeMap<String, Integer> compute() {
        List<NodeHref> taskList = new ArrayList<>();

        TreeMap<String, Integer> hrefContainer = new TreeMap<>();

        Integer levelNext = level + 1;
        String split = "/";

        try {
            Thread.sleep(100);
            Document document = Jsoup.connect(url).maxBodySize(0).get();
            Elements elements = document.select("a[href]");
            for (Element element: elements){
                String text = element.attr("href");
                if (text.matches(url + ".*/")){
                    String[] textSplit = text.split(split);
                    //Take the first level
                    text = "";
                    for (int i = 0; i < textSplit.length; i++) {
                        if (i > (2 + levelNext)){ break; }
                        if (text != ""){ text = text + split; }
                        text = text + textSplit[i];
                    }
                    text = text + split;

                    hrefContainer.put(text, levelNext);

                    NodeHref task = new NodeHref(text, levelNext);
                    task.fork();
                    taskList.add(task);
                }
            }

            for (NodeHref task : taskList){
                hrefContainer.putAll(task.join());
            }
        } catch (IOException | InterruptedException e) {
            //throw new RuntimeException(e);
        }

        return hrefContainer;
    }
}
