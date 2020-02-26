package com.quockhanhng.training.insta.Model;

public class User {
    private String id;
    private String username;
    private String fullName;
    private String imgUrl;
    private String bio;

    public User() {
    }

    public User(String id, String username, String fullName, String imgUrl, String bio) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.imgUrl = imgUrl;
        this.bio = bio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
