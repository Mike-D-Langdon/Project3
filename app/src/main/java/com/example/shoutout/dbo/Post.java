package com.example.shoutout.dbo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Post {

    private UUID id;
    private UUID parent;
    private UUID user;
    private String text;
    private List<UUID> images;
    private LocalDateTime posted;
    private int likes;
    private int comments;

    public Post(UUID id, UUID parent, UUID user, String text, List<UUID> images, LocalDateTime posted, int likes, int comments) {
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getParent() {
        return parent;
    }

    public void setParent(UUID parent) {
        this.parent = parent;
    }

    public UUID getUser() {
        return user;
    }

    public void setUser(UUID user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<UUID> getImages() {
        return images;
    }

    public void setImages(List<UUID> images) {
        this.images = images;
    }

    public LocalDateTime getPosted() {
        return posted;
    }

    public void setPosted(LocalDateTime posted) {
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
