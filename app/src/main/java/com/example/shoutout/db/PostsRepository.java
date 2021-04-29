package com.example.shoutout.db;

import android.net.Uri;
import android.util.Log;

import com.example.shoutout.dbo.Post;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Repository for uploading, managing, and retrieving posts from a Firebase Firestore server.
 *
 * @author Corneilious Eanes
 * @since April 29, 2021
 */
public class PostsRepository extends BaseFirestoreRepository {

    /**
     * Logging tag
     */
    private static final String TAG = PostsRepository.class.getSimpleName();

    /**
     * Constructor which requires a reference to the Firebase Firestore collection
     * @param col A reference to the Firebase Firestore collection
     */
    public PostsRepository(CollectionReference col) {
        super(col);
    }

    /**
     * Asynchronously fetch a single post based on its ID
     * @param id The ID of the post to fetch
     * @return A task containing the post. If the post is null, the ID did not correspond to
     * anything.
     */
    public Task<Post> get(String id) {
        return getCollection()
                .document(id)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot ds = task.getResult();
                        if (ds != null) {
                            return fromDbo(ds);
                        }
                    }
                    return null;
                });
    }

    /**
     * Asynchronously create a new post.
     * @param parent The parent in which this post belongs to (currently unused, just set it to
     *               null)
     * @param user The ID of the user that uploaded this post
     * @param text The text body of this post
     * @param images The paths of the images the user wishes to attach (should be the path
     *               retrieved from {@link ImagesRepository#upload(File)} or
     *               {@link ImagesRepository#upload(Uri)}.
     * @return A void task
     */
    public Task<Void> create(String parent, String user, String text, List<String> images) {
        return getCollection()
                .document()
                .set(new Post(parent, user, text, images, new Date(), 0, 0));
    }

    /**
     * Asynchronously fetch a collection of posts from a single user. Posts are ordered by post
     * date ({@link Post#getPosted()}) in descending order, so the latest post will be the 0th
     * element. This should be used when building a view of a single user's profile.
     * @param userId The ID of the user whose posts to fetch
     * @param limit The maximum number of posts to fetch
     * @param before The cutoff point before looking for more posts
     * @return A task of a list of posts. This list will be empty if either 1) the user has not
     * posted anything, or 2) something went wrong during the request.
     */
    public Task<List<Post>> getPostsFromUser(String userId, int limit, Date before) {
        return getCollection()
                .whereEqualTo("user", userId)
                .orderBy("posted", Query.Direction.DESCENDING)
                .startAfter(before)
                .limit(limit)
                .get()
                .continueWith(task -> {
                   if (task.isSuccessful()) {
                       return fromDbos(task.getResult());
                   }
                   return Collections.emptyList();
                });
    }

    /**
     * Asynchronously fetch a collection of posts from several users at once. Posts are ordered by
     * post date ({@link Post#getPosted()}) in descending order, so the latest post will be the 0th
     * element. This should be used when building the local user's timeline of posts.
     * @param users List of users to fetch posts from
     * @param limit The maximum number of posts to fetch
     * @param before The cutoff point before looking for more posts
     * @return A task of a list of points. This list will be empty is either 1) none of the
     * specified user has uploaded anything, or 2) if something went wrong during the request.
     */
    public Task<List<Post>> getPostsFromUsers(List<String> users, int limit, Date before) {
        return getCollection()
                .whereIn("user", users)
                .orderBy("posted", Query.Direction.DESCENDING)
                .startAfter(before)
                .limit(limit)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        return fromDbos(task.getResult());
                    }
                    return Collections.emptyList();
                });
    }

    /**
     * Asynchronously increase the like count of a post by 1.
     * @param postId The post ID whose like count should be increased by 1
     * @return A task containing a boolean. This boolean will be true if the request was successful,
     * false otherwise.
     */
    public Task<Boolean> addLike(String postId) {
        return get(postId).onSuccessTask(post -> {
            if (post != null) {
                return getCollection()
                        .document(postId)
                        .update("likes", post.getLikes() + 1)
                        .continueWith(Task::isSuccessful);
            }
            return Tasks.forResult(false);
        });
    }

    /**
     * Asynchronously decrease the like count of a post by 1.
     * @param postId The post ID whose like count should be decreased by 1
     * @return A task containing a boolean. This boolean will be true if the request was successful,
     * false otherwise.
     */
    public Task<Boolean> removeLike(String postId) {
        return get(postId).onSuccessTask(post -> {
           if (post != null) {
               return getCollection()
                       .document(postId)
                       .update("likes", post.getLikes() - 1)
                       .continueWith(Task::isSuccessful);
           }
           return Tasks.forResult(false);
        });
    }

    /**
     * Safely convert an instance of {@link DocumentSnapshot} into a more dev-friendly {@link Post}
     * object.
     * @param ds The document snapshot to convert
     * @return A converted object if the conversion was successful, false otherwise
     */
    private static Post fromDbo(DocumentSnapshot ds) {
        try {
            Post post = ds.toObject(Post.class);
            post.setId(ds.getId());
            return post;
        } catch (Exception e) {
            Log.w(TAG, "Could not parse post", e);
        }
        return null;
    }

    /**
     * Safely convert a list of {@link DocumentSnapshot} stored in a query.
     * @param qs The query snapshot containing the document snapshots to convert
     * @return A list of converted objects. Invalid objects will be kept as null values
     * @see #fromDbo(DocumentSnapshot)
     */
    private static List<Post> fromDbos(QuerySnapshot qs) {
        if (qs.isEmpty()) {
            return Collections.emptyList();
        }
        List<Post> posts = new ArrayList<>();
        for (DocumentSnapshot ds : qs.getDocuments()) {
            posts.add(fromDbo(ds));
        }
        return posts;
    }

    /**
     * A static instance of this repository for ease of use
     */
    private static PostsRepository instance = null;

    /**
     * Retrieve a static, permanent, non-null instance of this repository for ease of use
     * @return A permanent instance of this repository
     */
    public static PostsRepository getInstance() {
        if (instance == null) {
            instance = new PostsRepository(FirebaseFirestore.getInstance().collection("posts"));
        }
        return instance;
    }

}
