package com.example.fmcarer.Model;

public class Post {
    private String postid;
    private String postimage;
    private String description;
    private String publisher;
    private String status; // approved / rejected / pending
    private String shareLevel; // family / community
    private String userRole;  // Thêm mới: Vai trò của người đăng ("main" hoặc "sub")

    public Post() {}

    // Getters and setters
    public String getPostid() { return postid; }
    public void setPostid(String postid) { this.postid = postid; }

    public String getPostimage() { return postimage; }
    public void setPostimage(String postimage) { this.postimage = postimage; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getShareLevel() { return shareLevel; }
    public void setShareLevel(String shareLevel) { this.shareLevel = shareLevel; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }
}
