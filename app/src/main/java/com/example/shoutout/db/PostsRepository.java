package com.example.shoutout.db;

import android.util.Log;

import com.example.shoutout.dbo.Post;
import com.example.shoutout.util.DateTimeUtil;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PostsRepository {

    private static final String TAG = "PostRepo";

    private CollectionReference col;

    public PostsRepository(CollectionReference col) {
        this.col = col;
    }

    public Optional<Post> get(UUID id) {
        DocumentSnapshot result = col.document(id.toString()).get().getResult();
        if (result.exists()) {
            return Optional.of(convert(result.toObject(PostDBO.class)));
        }
        return Optional.empty();
    }

    public void create(UUID parent, UUID user, String text, List<UUID> images) {
        UUID id = UUID.randomUUID();
        Post post = new Post(id, parent, user, text, images, LocalDateTime.now(), 0, 0);
        PostDBO dbo = new PostDBO(post);
        col.document(dbo.id).set(dbo)
                .addOnSuccessListener(task -> Log.i(TAG, String.format("New post posted: id: %s, parent: %s, user: %s", id, post.getParent(), post.getUser())))
                .addOnFailureListener(e -> Log.w(TAG, "Post could not be created", e));
    }

    public List<Post> getPostsFromUser(UUID userId, int limit, LocalDateTime after) {
        return col.whereEqualTo("user", userId.toString()).orderBy("posted", Query.Direction.DESCENDING).startAfter(after).limit(limit)
                .get().getResult().toObjects(PostDBO.class).stream().map(PostsRepository::convert).collect(Collectors.toList());
    }

    public List<Post> getPostsFromUsers(List<UUID> users, int limit, LocalDateTime after) {
        return col.whereIn("user", users.stream().map(UUID::toString).collect(Collectors.toList())).orderBy("likes", Query.Direction.DESCENDING).startAfter(after).limit(limit)
                .get().getResult().toObjects(PostDBO.class).stream().map(PostsRepository::convert).collect(Collectors.toList());
    }

    public List<Post> getPostsFromParent(UUID parent, int limit) {
        //return get(parent);
        // FIXME actually return something
        return Collections.emptyList();
    }

    private static class PostDBO {
        public String id;
        public String parent;
        public String user;
        public String text;
        public List<String> images;
        public long posted;
        public int likes;
        public int comments;
        public PostDBO(Post post) {
            id = post.getId().toString();
            parent = post.getParent().toString();
            user = post.getUser().toString();
            text = post.getText();
            images = post.getImages().stream().map(UUID::toString).collect(Collectors.toList());
            posted = DateTimeUtil.formatDateTime(post.getPosted());
            likes = post.getLikes();
            comments = post.getComments();
        }
    }

    private static Post convert(PostDBO post) {
        return new Post(
                UUID.fromString(post.id),
                UUID.fromString(post.parent),
                UUID.fromString(post.user),
                post.text,
                post.images.stream().map(UUID::fromString).collect(Collectors.toList()),
                DateTimeUtil.parseDateTime(post.posted),
                post.likes,
                post.comments
        );
    }

}
