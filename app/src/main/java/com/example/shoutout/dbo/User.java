package com.example.shoutout.dbo;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class User {

    private String id;
    private String username;
    private String email;
    private String displayName;
    private String biography;
    private Date created;
    private Date birthday;
    private int permissionLevel;
    private List<String> following;
    private List<String> likes;
    private List<String> followers;

    public User(String id, String username, String email, String displayName, String biography,
                Date created, Date birthday, int permissionLevel, List<String> following,
                List<String> likes, List<String> followers) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.biography = biography;
        this.created = created;
        this.birthday = birthday;
        this.permissionLevel = permissionLevel;
        this.following = following;
        this.likes = likes;
        this.followers = followers;
    }

    public User(String id, String username, String email) {
        this(id, username, email, username, "", new Date(), null, 0, Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList());
    }

    public User() {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(int permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public List<String> getFollowing() {
        return following;
    }

    public void setFollowing(List<String> following) {
        this.following = following;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }
}
