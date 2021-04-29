package com.example.shoutout.dbo;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Outlines all the basic details present in a post.
 *
 * @author Corneilious Eanes
 * @since April 29, 2021
 */
public class Post implements Serializable {

    private String id;
    private String parent;
    private String user;
    private String text;
    private List<String> images;
    private Date posted;
    private int likes;
    private int comments;

    /**
     * Full constructor that initializes all fields.
     * @param parent The parent that this post belongs to (currently unused, set to null)
     * @param user The ID of the user that uploaded this post
     * @param text The text body of this post (optional)
     * @param images List of images attached to this post
     * @param posted When this post was uploaded
     * @param likes Number of user that liked this post
     * @param comments Number of comments present for this post (currently unused)
     */
    public Post(String parent, String user, String text, List<String> images, Date posted, int likes, int comments) {
        this.parent = parent;
        this.user = user;
        this.text = text;
        this.images = images;
        this.posted = posted;
        this.likes = likes;
        this.comments = comments;
    }

    /**
     * Basic default constructor that doesn't explicitly initialize any fields.
     */
    public Post() {
    }

    /**
     * Get the ID of this post
     * @return The ID of this post
     */
    @Exclude
    public String getId() {
        return id;
    }

    /**
     * Set the ID of this post
     * @param id The new ID of this post
     */
    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the parent of this post (not used)
     * @return The parent of this post
     */
    public String getParent() {
        return parent;
    }

    /**
     * Set the parent of this post (not used)
     * @param parent The new parent of this post
     */
    public void setParent(String parent) {
        this.parent = parent;
    }

    /**
     * Get the user ID of whomever uploaded this post
     * @return The user ID of whomever uploaded this post
     */
    public String getUser() {
        return user;
    }

    /**
     * Set the user ID of whomever uploaded this post
     * @param user The new user ID of whomever uploaded this post
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Get the text body of this post (may be empty)
     * @return The text body of this post
     */
    public String getText() {
        return text;
    }

    /**
     * Set the text body of this post
     * @param text The new text body for this post
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Get the list of images attached to this post (currently will only be empty or contain 1
     * element).
     * @return The list of images attacehd to this post
     */
    public List<String> getImages() {
        return images;
    }

    /**
     * Set the list of images attached to this post
     * @param images The new list of images attached to this post
     */
    public void setImages(List<String> images) {
        this.images = images;
    }

    /**
     * Get the date of which this post was uploaded
     * @return The date of which this post was uploaded
     */
    public Date getPosted() {
        return posted;
    }

    /**
     * Set the date of which this post was uploaded
     * @param posted The new date of which this post was uploaded
     */
    public void setPosted(Date posted) {
        this.posted = posted;
    }

    /**
     * Get the number of users that liked this post
     * @return The number of users that liked this post
     */
    public int getLikes() {
        return likes;
    }

    /**
     * Set the number of likes that this post has
     * @param likes The new number of likes that this post has
     */
    public void setLikes(int likes) {
        this.likes = likes;
    }

    /**
     * Get the number of comments this post has
     * @return The number of comments this post has
     */
    public int getComments() {
        return comments;
    }

    /**
     * Set the number of comments this post has
     * @param comments The new number of comments this post has
     */
    public void setComments(int comments) {
        this.comments = comments;
    }

}
