package com.company.neophite.parser.model;

public class NodeOfPath {

    private String date;
    private String info;

    public NodeOfPath( String date, String info) {
        this.date = date;
        this.info = info;
    }

    public NodeOfPath() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return
                "date='" + date + '\'' +
                ", info='" + info + '\'' ;
    }
}