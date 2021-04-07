package com.example.shoutout.dbo;

import java.time.LocalDateTime;
import java.util.UUID;

public class Login {

    private UUID token;
    private UUID userId;
    private String userAgent;
    private LocalDateTime created;
    private LocalDateTime expires;

    public Login(UUID token, UUID userId, String userAgent, LocalDateTime created, LocalDateTime expires) {
        this.token = token;
        this.userId = userId;
        this.userAgent = userAgent;
        this.created = created;
        this.expires = expires;
    }

    public Login() {
    }

    public UUID getToken() {
        return token;
    }

    public void setToken(UUID token) {
        this.token = token;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getExpires() {
        return expires;
    }

    public void setExpires(LocalDateTime expires) {
        this.expires = expires;
    }

}
