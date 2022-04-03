package com.sabikrahat.studyzone.models;

public class Post {
    private String postId;
    private String dateTime;
    private String description;
    private String publisherId;

    public Post() {
    }

    public Post(String postId, String dateTime, String description, String publisherId) {
        this.postId = postId;
        this.dateTime = dateTime;
        this.description = description;
        this.publisherId = publisherId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }
}

