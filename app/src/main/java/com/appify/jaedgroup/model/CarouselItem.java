package com.appify.jaedgroup.model;

public class CarouselItem {
    private String id;
    private String imgUrl;

    public CarouselItem() {
    }

    public CarouselItem(String id, String imgUrl) {
        this.id = id;
        this.imgUrl = imgUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
