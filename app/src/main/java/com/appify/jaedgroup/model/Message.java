package com.appify.jaedgroup.model;

public class Message {
    private String id;
    private String name;
    private String email;
    private String userId;
    private String message;

    public Message() {
    }

    public Message(String id, String name, String email, String userId, String message) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.userId = userId;
        this.message = message;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
