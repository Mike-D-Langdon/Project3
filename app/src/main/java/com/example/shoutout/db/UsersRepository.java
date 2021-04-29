package com.example.shoutout.db;

import android.util.Log;

import com.example.shoutout.dbo.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UsersRepository extends BaseFirestoreRepository {

    private static final String TAG = "UserRepo";
    private static final String DEFAULT_AVATAR = "avatartest.png";

    public UsersRepository(CollectionReference col) {
        super(col);
    }

    public Task<Boolean> registerNewUser(String id, String username, String email) {
        final User user = new User(id, DEFAULT_AVATAR, username, email);
        return isUsernameAvailable(user.getUsername()).onSuccessTask(usernameAvailable -> {
            if (usernameAvailable) {
                return isEmailAvailable(user.getEmail()).onSuccessTask(emailAvailable -> {
                    if (emailAvailable) {
                        return getCollection()
                                .document(id)
                                .set(toDbo(user))
                                .continueWith(Task::isSuccessful);
                    }
                    return Tasks.forResult(false);
                });
            }
            return Tasks.forResult(false);
        });
    }

    public Task<Void> updateUsername(String id, String newUsername) {
        return getCollection()
                .document(id)
                .update("username", newUsername, "username_lc", newUsername.toLowerCase());
    }

    public Task<Void> updateEmail(String id, String newEmail) {
        return getCollection()
                .document(id)
                .update("email", newEmail, "email_lc", newEmail.toLowerCase());
    }

    public Task<Void> updateDisplayName(String id, String newDisplayName) {
        return getCollection()
                .document(id)
                .update("displayName", newDisplayName, "displayName_lc", newDisplayName.toLowerCase());
    }

    public Task<Void> updateBiography(String id, String newBiograpy) {
        return getCollection()
                .document(id)
                .update("biography", newBiograpy);
    }

    public Task<Void> updateBirthday(String id, Date newBirthday) {
        return getCollection()
                .document(id)
                .update("birthday", newBirthday);
    }

    public Task<Void> updateAvatar(String id, String newAvatarPath) {
        return getCollection()
                .document(id)
                .update("avatar", newAvatarPath);
    }

    public Task<Boolean> follow(String followingUserId, String followedUserId) {
        // following = users that this user follows
        // followers = users that follow this user
        return get(followingUserId).onSuccessTask(user -> {
            // first update the first user's following list
            if (!user.getFollowing().contains(followedUserId)) {
                user.getFollowing().add(followedUserId);
                return getCollection()
                        .document(followingUserId)
                        .update("following", user.getFollowing())
                        .onSuccessTask(dummy -> get(followedUserId).onSuccessTask(followedUser -> {
                            // next, for the user that's being followed, update their followers list
                            followedUser.getFollowers().add(followingUserId);
                            return getCollection().document(followedUserId)
                                    .update("followers", followedUser.getFollowers())
                                    .continueWith(t -> true);
                        }));
            } else {
                Log.d(TAG, followingUserId + " attempted to re-follow " + followedUserId);
            }
            return Tasks.forResult(false);
        });
    }

    public Task<Boolean> unfollow(String unfollowingUserId, String unfollowedUserId) {
        return get(unfollowingUserId).onSuccessTask(unfollowingUser -> {
            if (unfollowingUser.getFollowing().remove(unfollowedUserId)) {
                return getCollection().document(unfollowingUserId).update("following", unfollowingUser.getFollowing()).onSuccessTask(dummy -> get(unfollowedUserId).onSuccessTask(unfollowedUser -> {
                   if (unfollowedUser.getFollowers().remove(unfollowingUserId)) {
                       return getCollection().document(unfollowedUserId).update("followers", unfollowedUser.getFollowers()).continueWith(t -> true);
                   } else {
                       Log.d(TAG, "Attempted to remove " + unfollowingUserId + " from " + unfollowedUserId + "'s followers list, but they're not there");
                   }
                   return Tasks.forResult(false);
                }));
            } else {
                Log.d(TAG, unfollowingUserId + " attempted to unfollow " + unfollowedUserId + ", whom they don't follow");
            }
            return Tasks.forResult(false);
        });
    }

    public Task<Boolean> like(String userId, String postId) {
        // will not update the post's like count!
        return get(userId).onSuccessTask(user -> {
           List<String> newLikes = new ArrayList<>(user.getLikes());
           if (!newLikes.contains(postId)) {
               newLikes.add(postId);
               return getCollection().document(userId).update("likes", newLikes).continueWith(t -> true);
           } else {
               Log.d(TAG, "Attempted to like a post that is already liked");
           }
           return Tasks.forResult(false);
        });
    }

    public Task<Boolean> unlike(String userId, String postId) {
        // will not update the post's like count!
        return get(userId).onSuccessTask(user -> {
           List<String> newLikes = new ArrayList<>(user.getLikes());
           if (newLikes.remove(postId)) {
               return getCollection().document(userId).update("likes", newLikes).continueWith(t -> true);
           } else {
               Log.d(TAG, "Attempted to unlike a post that isn't already liked");
           }
           return Tasks.forResult(false);
        });
    }

    public Task<Boolean> isUsernameAvailable(String username) {
        return getCollection().whereEqualTo("username", username).get().continueWith(task -> {
            if (task.isSuccessful()) {
                return task.getResult().isEmpty();
            }
            return false;
        });
    }

    public Task<Boolean> isEmailAvailable(String email) {
        return getCollection().whereEqualTo("email", email).get().continueWith(task -> {
            if (task.isSuccessful()) {
                return task.getResult().isEmpty();
            }
            return false;
        });
    }

    public Task<User> get(String id) {
        return getCollection().document(id).get().continueWith(task -> {
            if (task.isSuccessful()) {
                try {
                    DocumentSnapshot ds = task.getResult();
                    if (ds != null) {
                        return fromDbo(ds);
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Could not parse document snapshot as user", e);
                }
            }
            return null;
        });
    }

    public Task<User> getFromUsername(String username) {
        return getCollection().whereEqualTo("username", username.toLowerCase()).get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot qs = task.getResult();
                        if (!qs.isEmpty()) {
                            return fromDbo(qs.getDocuments().get(0));
                        }
                    }
                    return null;
                });
    }

    public Task<User> getFromEmail(String email) {
        return getCollection().whereEqualTo("email", email.toLowerCase()).get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot qs = task.getResult();
                        if (!qs.isEmpty()) {
                            return fromDbo(qs.getDocuments().get(0));
                        }
                    }
                    return null;
                });
    }

    private static class UserDbo {
        public String avatar;
        public String username;
        public String username_lc;
        public String email;
        public String email_lc;
        public String displayName;
        public String displayName_lc;
        public String biography;
        public Date created;
        public Date birthday;
        public int permissionLevel;
        public List<String> following;
        public List<String> likes;
        public List<String> followers;

        public UserDbo(User user) {
            avatar = user.getAvatarUri();
            username = user.getUsername();
            username_lc = username.toLowerCase();
            email = user.getEmail();
            email_lc = email.toLowerCase();
            displayName = user.getDisplayName();
            displayName_lc = displayName.toLowerCase();
            biography = user.getBiography();
            created = user.getCreated();
            birthday = user.getBirthday();
            permissionLevel = user.getPermissionLevel();
            following = user.getFollowing();
            likes = user.getLikes();
            followers = user.getFollowers();
        }
        public UserDbo() {
        }
    }

    private static UserDbo toDbo(User user) {
        return new UserDbo(user);
    }

    private static User fromDbo(DocumentSnapshot ds) {
        try {
            UserDbo dbo = ds.toObject(UserDbo.class);
            return new User(ds.getId(), dbo.avatar, dbo.username, dbo.email, dbo.displayName,
                    dbo.biography, dbo.created, dbo.birthday, dbo.permissionLevel, dbo.following,
                    dbo.likes, dbo.followers
            );
        } catch (Exception e) {
            Log.w(TAG, "Could not parse user", e);
        }
        return null;
    }

    private static UsersRepository instance = null;

    public static UsersRepository getInstance() {
        if (instance == null) {
            instance = new UsersRepository(FirebaseFirestore.getInstance().collection("users"));
        }
        return instance;
    }

}
