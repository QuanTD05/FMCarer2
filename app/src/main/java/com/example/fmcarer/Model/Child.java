package com.example.fmcarer.Model;

import java.io.Serializable;

public class Child  implements Serializable {
    private String childId;
    private String name;
    private int age;
    private String gender;
    private String birthdate;
    private String address;
    private String imageUrl;
    private String userId;

    public Child() {
        // Required for Firebase
    }

    public Child(String childId, String name, int age, String gender, String birthdate, String address, String imageUrl) {
        this.childId = childId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.birthdate = birthdate;
        this.address = address;
        this.imageUrl = imageUrl;
    }

    // Getter và Setter
    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
