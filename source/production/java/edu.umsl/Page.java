package edu.umsl;

public class Page implements Comparable{
    private String url; //url of page
    private String title; //title of page
    private String origin; //page this link was scraped from
    private int NumLinks;
    private Object o;

    public Page(String og,String u){
        origin = og;
        url = u;
    }
    public String getUrl(){
        return url;
    }
    public String getTitle(){
        return title;
    }
    public String getOrigin(){
        return origin;
    }
    public int getNumLinks(){
        return NumLinks;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setNumLinks(int n){
        NumLinks = n;
    }

    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public String toString() {
        return "Page{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", origin='" + origin + '\'' +
                ", NumLinks=" + NumLinks +
                '}';
    }
}
