package com.appify.jaedgroup.model;

public class EstateImage {
    private String id;
    private String imageUrl;
    private String imageId;

    public EstateImage() {
    }

    public EstateImage(String id, String imageUrl, String imageId) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.imageId = imageId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}
