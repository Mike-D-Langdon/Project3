package com.example.shoutout.db;

import android.util.Log;

import com.example.shoutout.dbo.Post;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PostsRepository extends BaseFirestoreRepository {

    private static final String TAG = "PostRepo";

    public PostsRepository(CollectionReference col) {
        super(col);
    }

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

    public Task<Void> create(String parent, String user, String text, List<String> images) {
        return getCollection()
                .document()
                .set(new Post(parent, user, text, images, new Date(), 0, 0));
    }

    public Task<List<Post>> getPostsFromUser(String userId, int limit, Date after) {
        return getCollection()
                .whereEqualTo("user", userId)
                .orderBy("posted")
                .startAfter(after)
                .limit(limit)
                .get()
                .continueWith(task -> {
                   if (task.isSuccessful()) {
                       return fromDbos(task.getResult());
                   }
                   return Collections.emptyList();
                });
    }

    public Task<List<Post>> getPostsFromUsers(List<String> users, int limit, Date after) {
        return getCollection()
                .whereIn("user", users)
                .orderBy("posted")
                .startAfter(after)
                .limit(limit)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        return fromDbos(task.getResult());
                    }
                    return Collections.emptyList();
                });
    }

    public Task<List<Post>> getPostsFromParent(String parent, int limit, Date after) {
        return getCollection().whereEqualTo("parent", parent).limit(limit)
                .get().continueWith(task -> {
                    if (task.isSuccessful()) {
                        return fromDbos(task.getResult());
                    }
                    return Collections.emptyList();
                });
    }

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

    private static PostsRepository instance = null;

    public static PostsRepository getInstance() {
        if (instance == null) {
            instance = new PostsRepository(FirebaseFirestore.getInstance().collection("posts"));
        }
        return instance;
    }

}
