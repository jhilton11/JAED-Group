package com.appify.jaedgroup.model;

import java.io.Serializable;

public class EstatesInfo implements Serializable {
    private String id;
    private String name;
    private String price;
    private String size;

    public EstatesInfo() {
    }

    public EstatesInfo(String id, String name, String price, String size) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
