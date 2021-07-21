package edu.umsl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.print.attribute.standard.NumberUp;
import java.io.WriteAbortedException;
import java.util.*;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WebCrawler{
    private String rootLink;
    protected static BlockingQueue<Page> UnvisitedPages = new PriorityBlockingQueue<>();
    protected static ConcurrentHashMap<String,Page> VisitedPages = new ConcurrentHashMap<>();
    protected static ConcurrentHashMap<String,Boolean> FourOhFours = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String,Integer> WordFrequencyMap = new ConcurrentHashMap<>();
    private static int numThreads;
    private static int numLinks;
    public WebCrawler(String root,int nthreads,int nlinks){
        numThreads = nthreads;
        numLinks = nlinks;
        Page startPage = new Page(root,root);
        rootLink = root;
        UnvisitedPages.add(startPage);
    }
    public static void UpdateWordCount(String n){
        Integer f = WordFrequencyMap.get(n);
        if(f == null){
            WordFrequencyMap.put(n,1);
        }else{
            WordFrequencyMap.put(n,f+1);
        }
    }
    private static HashMap sortByValues(ConcurrentHashMap<String, Integer> map) {
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }

        return sortedHashMap;
    }
    public void PrintWordFrequency() {
        HashMap SortedMap = sortByValues(WordFrequencyMap);
        Iterator MapIter = SortedMap.keySet().iterator();
        while(MapIter.hasNext()){
            String key = (String) MapIter.next();
            System.out.println(key + ":" + WordFrequencyMap.get(key));
        }
    }
    public void PrintVisitedPageTitles() {
        VisitedPages.values().forEach(n -> System.out.println(n.getTitle()));
        System.out.println("Visited :"+ VisitedPages.size());
    }
    public static boolean Incomplete() {
        return VisitedPages.size() < numLinks;
    }
    public void CrawlURL() throws IOException {
        List<Thread> pageProcessors = new LinkedList<>();
        //create n threads with respective id.
        for(int i = 0; i< numThreads;i++){
            pageProcessors.add(new Thread(new PageProcessor(i)));
        }
        //start the threads
        pageProcessors.forEach(n -> n.start());
        pageProcessors.forEach(n -> {
            try {
                n.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

    }




}
