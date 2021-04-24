package com.example.shoutout.dbo;

import com.google.firebase.firestore.Exclude;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Post {

    @Exclude
    private String id;
    private String parent;
    private String user;
    private String text;
    private List<String> images;
    private Date posted;
    private int likes;
    private int comments;

    public Post(String parent, String user, String text, List<String> images, Date posted, int likes, int comments) {
        this.id = id;
        this.parent = parent;
        this.user = user;
        this.text = text;
        this.images = images;
        this.posted = posted;
        this.likes = likes;
        this.comments = comments;
    }

    public Post() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Date getPosted() {
        return posted;
    }

    public void setPosted(Date posted) {
        this.posted = posted;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

}
