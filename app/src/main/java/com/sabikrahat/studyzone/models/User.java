package com.sabikrahat.studyzone.models;

public class User {
    private String uid;
    private String rid;
    private String email;
    private String name;
    private String phone;
    private String imageURL;
    private String batch;
    private String role;
    private String status;
    private String createAt;

    public User() {
    }

    public User(String uid, String rid, String email, String name, String phone, String imageURL, String batch, String role, String status, String createAt) {
        this.uid = uid;
        this.rid = rid;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.imageURL = imageURL;
        this.batch = batch;
        this.role = role;
        this.status = status;
        this.createAt = createAt;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }
}

