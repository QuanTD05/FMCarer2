package com.example.fmcarer.Model;


public class User {
    private String id;
    private String fullname;
    private String username;
    private String bio;
    private String imageurl;
    private String role;

    public User() {}

    public User(String id, String fullname, String username, String bio, String imageurl, String role) {
        this.id = id;
        this.fullname = fullname;
        this.username = username;
        this.bio = bio;
        this.imageurl = imageurl;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getFullname() {
        return fullname;
    }

    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

    public String getImageurl() {
        return imageurl;
    }

    public String getRole() {
        return role;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
