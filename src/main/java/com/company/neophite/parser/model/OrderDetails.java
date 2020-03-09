package com.company.neophite.parser.model;


import java.util.List;

public class OrderDetails {
    private String from;
    private String to;
    private String orderService;
    private String weight;
    private int onTheWay;
    private String arrivalTime;
    private List<NodeOfPath> pathList;

    public OrderDetails() {
    }

    public OrderDetails(String from, String to, String orderService, String weight, int onTheWay, String arrivalTime, List<NodeOfPath> pathList) {
        this.from = from;
        this.to = to;
        this.orderService = orderService;
        this.weight = weight;
        this.onTheWay = onTheWay;
        this.arrivalTime = arrivalTime;
        this.pathList = pathList;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getOrderService() {
        return orderService;
    }

    public void setOrderService(String orderService) {
        this.orderService = orderService;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public int getOnTheWay() {
        return onTheWay;
    }

    public void setOnTheWay(int onTheWay) {
        this.onTheWay = onTheWay;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public List<NodeOfPath> getPathList() {
        return pathList;
    }

    public void setPathList(List<NodeOfPath> pathList) {
        this.pathList = pathList;
    }
}
