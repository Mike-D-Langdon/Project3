package com.example.shoutout.dbo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class User {

    private UUID id;
    private String username;
    private String pwd;
    private String biography;
    private LocalDate birthday;
    private LocalDateTime created;
    private int permissionLevel;
    private List<UUID> following;

    public User(UUID id, String username, String pwd, String biography, LocalDate birthday, LocalDateTime created, int permissionLevel, List<UUID> following) {
        this.id = id;
        this.username = username;
        this.pwd = pwd;
        this.biography = biography;
        this.birthday = birthday;
        this.created = created;
        this.permissionLevel = permissionLevel;
        this.following = following;
    }

    public User() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public int getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(int permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public List<UUID> getFollowing() {
        return following;
    }

    public void setFollowing(List<UUID> following) {
        this.following = following;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
}
