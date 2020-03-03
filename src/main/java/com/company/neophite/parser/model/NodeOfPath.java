package com.company.neophite.parser.model;

import java.util.ArrayList;

public class NodeOfPath {

    private String date;
    private String info;
    private String postService;

    public NodeOfPath( String date, String info,String postService) {
        this.date = date;
        this.info = info;
        this.postService = postService;
    }

    public NodeOfPath() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostService() {
        return postService;
    }

    public void setPostService(String postService) {
        this.postService = postService;
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
                ", info='" + info + '\'' +
                ", postService='" + postService ;
    }
}