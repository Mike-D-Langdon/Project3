package com.example.shoutout.db;

import android.util.Log;

import com.example.shoutout.dbo.User;
import com.example.shoutout.util.DateTimeUtil;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class UsersRepository {

    private static final String TAG = "UserRepo";

    private CollectionReference col;

    public UsersRepository(CollectionReference col) {
        this.col = col;
    }

    public UUID registerNewUser(User user) {
        if (!isUsernameAvailable(user.getUsername())) {
            throw new IllegalArgumentException("Username is already taken: " + user.getUsername());
        }
        UUID id = UUID.randomUUID();
        user.setId(id);
        UserDBO dbo = new UserDBO(user);
        col.document(dbo.id).set(dbo)
                .addOnSuccessListener(task -> {
                    Log.i(TAG, String.format("Successfully registered new user: %s (%s)", user.getUsername(), id));
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, String.format("Could not register new user: %s", user.getUsername()), e);
                });
        return id;
    }

    public void updateUsername(UUID id, String newUsername) {
        if (!isUsernameAvailable(newUsername)) {
            throw new IllegalArgumentException("Username is already taken: " + newUsername);
        }
        col.document(id.toString())
                .update(
                        "username", newUsername,
                        "username_lc", newUsername.toLowerCase()
                )
                .addOnSuccessListener(task -> {
                    Log.i(TAG, String.format("Updated username of %s to %s", id, newUsername));
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, String.format("Could not update username for %s", id), e);
                });
    }

    public void updatePassword(UUID id, String pwd) {
        col.document(id.toString())
                .update("pwd", pwd)
                .addOnSuccessListener(task -> {
                    Log.i(TAG, String.format("Password updated for %s", id));
                })
                .addOnFailureListener(e -> {
                   Log.w(TAG, String.format("Could not update password for %s", id), e);
                });
    }

    public boolean checkPassword(String username, String passwordAttempt) {
        return getFromUsername(username)
                .map(user -> BCrypt.checkpw(passwordAttempt, user.getPwd()))
                .orElse(false);
    }

    public boolean isUsernameAvailable(String username) {
        // can also do #getFromUsername(String).isPresent(), but this doesn't convert anything
        return col.whereEqualTo("username_lc", username).get().getResult()
                .getDocuments().isEmpty();
    }

    public Optional<User> get(UUID id) {
        DocumentSnapshot result = col.document(id.toString()).get().getResult();
        if (result.exists()) {
            return Optional.of(convert(result.toObject(UserDBO.class)));
        }
        return Optional.empty();
    }

    public Optional<User> getFromUsername(String username) {
        return col.whereEqualTo("username_lc", username.toLowerCase())
                .get().getResult().getDocuments()
                .stream().findAny()
                .map(doc -> convert(doc.toObject(UserDBO.class)));
    }

    private static class UserDBO {
        public String id;
        public String username;
        public String username_lc;
        public String pwd;
        public String biography;
        public String birthday;
        public int permissionLevel;
        public long created;
        public List<String> following;
        public UserDBO(User user) {
            id = user.getId().toString();
            username = user.getUsername();
            username_lc = username.toLowerCase();
            pwd = user.getPwd();
            biography = user.getBiography();
            birthday = DateTimeUtil.formatDate(user.getBirthday());
            permissionLevel = user.getPermissionLevel();
            created = DateTimeUtil.formatDateTime(user.getCreated());
            following = user.getFollowing().stream().map(UUID::toString).collect(Collectors.toList());
        }
        public UserDBO() {
        }
    }

    private static User convert(UserDBO user) {
        return new User(
                UUID.fromString(user.id),
                user.username,
                user.pwd,
                user.biography,
                DateTimeUtil.parseDate(user.birthday),
                DateTimeUtil.parseDateTime(user.created),
                user.permissionLevel,
                user.following.stream().map(UUID::fromString).collect(Collectors.toList())
        );
    }

}
