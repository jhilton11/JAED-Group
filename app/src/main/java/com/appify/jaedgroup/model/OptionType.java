package com.appify.jaedgroup.model;

public class OptionType {
    private String id;
    private String type;
    private int price;

    public OptionType() {
    }

    public OptionType(String id, String type, int price) {
        this.id = id;
        this.type = type;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
