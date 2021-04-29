package com.example.shoutout.dbo;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Outlines all the basic information for a user, not including their hashed password, which is
 * handled by Firebase Auth.
 *
 * @author Corneilious Eanes
 * @since April 29, 2021
 */
public class User implements Serializable {

    private String id;
    private String avatarUri;
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

    /**
     * Constructor that initializes all fields.
     * @param id The ID (corresponds directly to {@link FirebaseUser#getUid()}
     * @param avatarUri The path of the avatar
     * @param username The username
     * @param email The email address
     * @param displayName The display name
     * @param biography The biography (may be empty)
     * @param created The date in which this account was created
     * @param birthday User's birthday (may be null)
     * @param permissionLevel Level of permission this user has (not used)
     * @param following List of user IDs that this user follows
     * @param likes List of post IDs that this user liked
     * @param followers List of user IDs that follow this user
     */
    public User(String id, String avatarUri, String username, String email, String displayName,
                String biography, Date created, Date birthday, int permissionLevel,
                List<String> following, List<String> likes, List<String> followers) {
        this.id = id;
        this.avatarUri = avatarUri;
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

    /**
     * A simpler constructor for creating a new user for registration.
     * @param id The ID (corresponds directly to {@link FirebaseUser#getUid()}
     * @param avatarUri The path of the avatar
     * @param username The username
     * @param email The email address
     */
    public User(String id, String avatarUri, String username, String email) {
        this(id, avatarUri, username, email, username, "", new Date(), null, 0,
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    /**
     * Basic default constructor that doesn't explicitly initialize any fields.
     */
    public User() {
    }

    /**
     * Get the ID of this user
     * @return The ID of this user
     */
    public String getId() {
        return id;
    }

    /**
     * Set the ID of this user
     * @param id The new ID of this user
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the path of this user's avatar
     * @return The path of this user's avatar
     * @see com.example.shoutout.db.ImagesRepository
     */
    public String getAvatarUri() {
        return avatarUri;
    }

    /**
     * Set the path of this user's avatar
     * @param avatarUri The new path of this user's avatar
     * @see com.example.shoutout.db.ImagesRepository
     */
    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
    }

    /**
     * Get this user's username
     * @return This user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set this user's username
     * @param username The new username for this user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get this user's email address
     * @return The email address of this user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set this user's email address
     * @param email The new email address for this user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get this user's display name
     * @return The display name for this user
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Set this user's display name
     * @param displayName The new display name for this user
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get this user's biography
     * @return The biography of this user
     */
    public String getBiography() {
        return biography;
    }

    /**
     * Set this user's biography
     * @param biography The new biography of this user
     */
    public void setBiography(String biography) {
        this.biography = biography;
    }

    /**
     * Get this user's registeration date
     * @return The registration date for this user
     */
    public Date getCreated() {
        return created;
    }

    /**
     * Set this user's registration date
     * @param created The new registration date for this user
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * Get this user's birthday
     * @return The bithday of this user
     */
    public Date getBirthday() {
        return birthday;
    }

    /**
     * Set this user's birthday
     * @param birthday The new birthday of this user
     */
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    /**
     * Get this user's permission level (currently unused)
     * @return The permission level of this user
     */
    public int getPermissionLevel() {
        return permissionLevel;
    }

    /**
     * Set this user's permission level (currently unused)
     * @param permissionLevel The new permission level for this user
     */
    public void setPermissionLevel(int permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    /**
     * Get this user's list of user IDs they they follow
     * @return The list of user IDs that this user follows
     */
    public List<String> getFollowing() {
        return following;
    }

    /**
     * Set this user's list of user IDs that they follow
     * @param following The new list of user IDs that this user follows
     */
    public void setFollowing(List<String> following) {
        this.following = following;
    }

    /**
     * Get this user's list of post IDs of posts they liked
     * @return The list of post IDs of posts this user liked
     */
    public List<String> getLikes() {
        return likes;
    }

    /**
     * Set this user's list of post IDs of posts they liked
     * @param likes The new list of post IDs of posts this user liked
     */
    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    /**
     * Get this user's list of user IDs that follow them
     * @return The list of user IDs that follow this user
     */
    public List<String> getFollowers() {
        return followers;
    }

    /**
     * Set this user's list of user IDs that follow them
     * @param followers The new list of user IDs that follow this user
     */
    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }
}
