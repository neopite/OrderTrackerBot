package com.company.neophite;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataParser {

    private static final String apiSecond = "?smartyBMode=2&_pjax=%23tracking-page";

    public static void main(String[] args) {
        List<NodeOfPath> ar =new DataParser().getFullPath("UB037183810LV");
    }

    public List<NodeOfPath> getFullPath(String trackNumber) {
        Document doc = getOrderPageByTrackNumber(trackNumber);
        List<NodeOfPath> arrayListOfPathNodes = new ArrayList<NodeOfPath>();
        Elements dateElements = doc.select("div.package-route-date");
        Elements infoElements = doc.select("div.package-route-info");

        for (int itter = 1; itter <= dateElements.size()- 1; itter++) {
            String postSerivice = infoElements.get(itter).select("div.package-route-post-service").text();
            String date = dateElements.get(itter).text();
            String info = infoElements.get(itter).text();
            arrayListOfPathNodes.add(new NodeOfPath(date,info,postSerivice));
        }
        return arrayListOfPathNodes;
    }

    private static Document getOrderPageByTrackNumber(String trackNumber) {
        String fullPath = System.getenv("SiteUrl") + trackNumber + apiSecond;
        Document document = null;
        try {
            document = Jsoup.connect(fullPath).userAgent("Google Chrome").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;
    }

}