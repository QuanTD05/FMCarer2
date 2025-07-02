package com.example.fmcarer.Model;

public class Sub {
    private String user_id;
    private String fullname;
    private String username;
    private String email;
    private String password;
    private String phone;
    private String role;
    private String creatorId;
    private String imageurl;

    public Sub() {}

    // Constructor cũ (7 tham số) vẫn giữ lại nếu bạn cần dùng ở nơi khác
    public Sub(String user_id, String fullName, String email, String password, String phone, String role, String creatorId) {
        this.user_id = user_id;
        this.fullname = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
        this.creatorId = creatorId;
    }

    // ✅ Constructor mới đầy đủ 9 tham số
    public Sub(String user_id, String fullName, String username, String email, String password, String phone, String imageUrl, String role, String creatorId) {
        this.user_id = user_id;
        this.fullname = fullName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.imageurl = imageUrl;
        this.role = role;
        this.creatorId = creatorId;
    }

    // Getter và Setter đầy đủ
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullName) {
        this.fullname = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}
