package edu.umsl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class proj6 {
    public static void main(String[] args) throws IOException {
            Scanner s = new Scanner(System.in);

            //probably should start here.
            //https://en.wikipedia.org/wiki/Special:Random
            String startLink = "https://en.wikipedia.org/wiki/Special:Random";
            long starttime = System.nanoTime();
            System.out.println("Starting crawl at " + startLink);
            WebCrawler wc = new WebCrawler(startLink,10,1000);
            wc.CrawlURL();
            wc.PrintVisitedPageTitles();
            wc.PrintWordFrequency();
            long endtime = System.nanoTime();
            System.out.println("Total running time :" + TimeUnit.NANOSECONDS.toSeconds(endtime - starttime));
        }

}
