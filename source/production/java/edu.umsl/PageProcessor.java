package edu.umsl;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class PageProcessor implements  Runnable{
    private String procId;
    private static Pattern WikiUrlPattern = Pattern.compile("\\/wiki\\/[\\w:]+");
    public PageProcessor(int id){
        procId = "Page processor :" + id;
    }
    @Override
    public void run() {
        Document page = null;
        System.out.println(procId + " starting");
        while (WebCrawler.Incomplete()) {
            if(WebCrawler.UnvisitedPages.isEmpty()){
                try {
                    Thread.sleep(1);
                    continue;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Page curPage = WebCrawler.UnvisitedPages.poll();
            System.out.println("Scraping link :" + curPage.getUrl());
            //System.out.println("Number of pages visited : " + WebCrawler.VisitedPages.size());
            try {
                page = Jsoup.connect(curPage.getUrl()).get();
            } catch (IllegalArgumentException e) {
                System.out.println("Incomplete link");
                continue;
            } catch (HttpStatusException e) {
                System.out.println("Http status error " + curPage.getUrl());
                //Avoid hitting 404s twice
                WebCrawler.FourOhFours.putIfAbsent(curPage.getUrl(),true);
                continue;
            }catch (UnsupportedMimeTypeException e){
                System.out.println("Unsupported mime error" + curPage.getUrl());
                continue;
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            //Only count a visit after the try catch, errors don't count
            WebCrawler.VisitedPages.putIfAbsent(curPage.getUrl(),curPage);
            curPage.setTitle(page.title());
            //Man I really like these stream interfaces
            page.select("p").parallelStream()
                    .map(this::GetText)
                    .flatMap(this::TokenizeLine)
                    .map(this::StandardizeWords)
                    .filter(n -> !n.equals(""))
                    .forEach(WebCrawler::UpdateWordCount);

            page.select("a").parallelStream()
                    .map(this::TrimURLElement).filter(this::UrlFilter)
                    .map(this::UrlComplete)
                    .map(p -> new Page(curPage.getUrl(), p))
                    .forEach(p -> WebCrawler.UnvisitedPages.add(p));
        }
    }
    private String TrimURLElement(Element n){
        return n.attr("href");
    }
    private boolean UrlFilter(String n) {
        return WikiUrlPattern.matcher(n).matches() &&
                !WebCrawler.VisitedPages.containsKey(n) &&
                !WebCrawler.FourOhFours.containsKey(n);
    }
    private String UrlComplete(String n){
        return "https://en.wikipedia.org" + n;
    }
    private Page CreateUnvisitedPage(String n,String og) {
        return new Page(og,n);
    }
    private Stream<String> TokenizeLine(String n){
        return Arrays.stream(n.split("\\s"));
    }
    private String StandardizeWords(String n){
        return n.replaceAll("\\W","").toLowerCase();
    }
    private String GetText(Element n){
        return n.text();
    }
}
