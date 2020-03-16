package com.company.neophite.parser;

import com.company.neophite.parser.model.NodeOfPath;
import com.company.neophite.parser.model.OrderDetails;
import com.vdurmont.emoji.EmojiParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DataParser {
    private static final String API_SECOND_PART_OF_URL = "?smartyBMode=2&_pjax=%23tracking-page";
    private Document document;
    private static String firstPartOfLink = System.getenv("siteUrlFirst");

    public DataParser(String trackNumber) {
        this.document = getOrderPageByTrackNumber(trackNumber, firstPartOfLink);

    }

    public DataParser() {
    }

    public List<NodeOfPath> getFullPath(Document document) {
        List<NodeOfPath> arrayListOfPathNodes = new ArrayList<NodeOfPath>();
        Elements dateElements = document.select("div.package-route-date");
        Elements infoElements = document.select("div.package-route-info");
        for (int itter = 1; itter < dateElements.size(); itter++) {
            String date = dateElements.get(itter).text();
            String info = infoElements.get(itter).text();
            arrayListOfPathNodes.add(new NodeOfPath(date, info));
        }
        return arrayListOfPathNodes;
    }

    private static Document getOrderPageByTrackNumber(String trackNumber, String aPartOfUrl) {
        String fullPath = aPartOfUrl + trackNumber + API_SECOND_PART_OF_URL;
        Document document = null;
        try {
            document = Jsoup.connect(fullPath).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;
    }

    public OrderDetails generateOrderDetails() {
        List<String> ar = getInfoAboutOrder(this.document);
        return new OrderDetails(
                 ar.get(0),      //From country
                 ar.get(2),      //To country
                 ar.get(1),      //Order service
                 ar.get(3),      //Order weight
                 Integer.parseInt(ar.get(4)), //Time on the way
                 ar.get(5),      //ArrivalTime
                 getFullPath( document));
    }

    public String toStringPath(List<NodeOfPath> listOfPath) {
        StringBuilder totalOrderPath = new StringBuilder();
         for (int itter = listOfPath.size()-1; itter >= 0; itter--) {
            totalOrderPath.append(EmojiParser.parseToUnicode(":arrow_down:"));
            totalOrderPath.append(EmojiParser.parseToUnicode(":clock10:")).append(listOfPath.get(itter).getDate()).append('\n').append("*Нахождение* : ").append(listOfPath.get(itter).getInfo()).append('\n');
        }
        return totalOrderPath.toString();
    }

    public List<String> getInfoAboutOrder(Document document) {
        List<String> infoList = new ArrayList<>();
        Elements elementsFromPackageInfoList = getPackageInfoList(document).select("ul");
        Elements elementsFromPackageInfoDelivery = getPackageInfoDelivery(document);
        for (int itter = 0; itter < 4; itter++) {
            infoList.add(elementsFromPackageInfoList.get(0).getElementsByTag("li").get(itter).select("div.package-info-list-content").text());
        }
        infoList.add(elementsFromPackageInfoDelivery.get(0).select("div.package-info-delivery-days-value").text());
        infoList.add(elementsFromPackageInfoDelivery.get(0).select("div.package-info-delivery-target-value").text());
        return infoList;
    }

    public Elements getPackageInfoList(Document document) {
        return document.select("div.package-info-list");
    }

    public Elements getPackageInfoDelivery(Document document) {
        return document.select("div.package-info-delivery");
    }

}