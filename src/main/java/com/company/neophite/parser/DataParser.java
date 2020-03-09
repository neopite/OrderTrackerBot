package com.company.neophite.parser;

import com.company.neophite.parser.model.NodeOfPath;
import com.company.neophite.parser.model.OrderDetails;
import com.vdurmont.emoji.EmojiParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;

import javax.print.Doc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DataParser {
    private static final String API_SECOND_PART_OF_URL = "?smartyBMode=2&_pjax=%23tracking-page";
    private Document document;
    private OrderDetails orderDetails = null;
    private static String secondLink = System.getenv("siteUrlFirst");

    public DataParser(String trackNumber) {
        this.document = getOrderPageByTrackNumber(trackNumber, secondLink);

    }

    public List<NodeOfPath> getFullPath(String trackNumber, Document document) {
        List<NodeOfPath> arrayListOfPathNodes = new ArrayList<NodeOfPath>();
        Elements dateElements = document.select("div.package-route-date");
        Elements infoElements = document.select("div.package-route-info");

        for (int itter = 1; itter <= dateElements.size() - 1; itter++) {
            String postSerivice = infoElements.get(itter).select("div.package-route-post-service").text();
            String date = dateElements.get(itter).text();
            String info = infoElements.get(itter).text();
            arrayListOfPathNodes.add(new NodeOfPath(date, info, postSerivice));
        }
        orderDetails.setPathList(arrayListOfPathNodes);
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

    public OrderDetails generateOrderDetails(String trackNumber) {
        Document document = this.document;
        orderDetails = new OrderDetails();
        orderDetails.setPathList(getFullPath(trackNumber, document));
        List<String> ar = getInfoAboutOrder(document);
        orderDetails.setFrom(ar.get(0));
        orderDetails.setOrderService(ar.get(1));
        orderDetails.setTo(ar.get(2));
        orderDetails.setWeight(ar.get(3));
        orderDetails.setOnTheWay(Integer.parseInt(ar.get(4)));
        orderDetails.setArrivalTime(ar.get(5));
        return orderDetails;
    }

    public StringBuilder toStringPath(OrderDetails orderDetails) {
        StringBuilder string = new StringBuilder();
        for (int itter = orderDetails.getPathList().size() - 1; itter > 0; itter--) {
            string.append(EmojiParser.parseToUnicode(":arrow_down:"));
            string.append(EmojiParser.parseToUnicode(":clock10:") + orderDetails.getPathList().get(itter).getDate() + '\n' + " **Нахождение**" + orderDetails.getPathList().get(itter).getInfo() + '\n');
        }
        return string;
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

    public OrderDetails getOrderDetails() {
        return orderDetails;
    }
}