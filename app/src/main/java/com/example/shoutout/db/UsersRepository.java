package com.example.shoutout.db;

import android.net.Uri;
import android.util.Log;

import com.example.shoutout.dbo.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Repository for registering new user, updating user information, and retrieving user information.
 *
 * @author Corneilious Eanes
 * @since April 29, 2021
 */
public class UsersRepository extends BaseFirestoreRepository {

    /**
     * Logging tag
     */
    private static final String TAG = UsersRepository.class.getSimpleName();
    /**
     * Default avatar to use for newly-registered users
     */
    private static final String DEFAULT_AVATAR = "avatartest.png";

    /**
     * Constructor which requires a reference to the Firebase Firestore collection
     * @param col Reference to the Firebase Firestore collection
     */
    public UsersRepository(CollectionReference col) {
        super(col);
    }

    /**
     * Asynchronously register a new user into the system. This should be performed after
     * {@link com.google.firebase.auth.FirebaseAuth#createUserWithEmailAndPassword(String, String)}
     * is called, and {@link FirebaseUser#getUid()} should be used as the ID argument for this
     * method.
     * @param id The ID of the user to register
     * @param username The username of the user to register
     * @param email The email address of the user to register
     * @return A task containing a boolean. This boolean will be true if the registration was
     * successful, false otherwise.
     */
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

    /**
     * Asynchronously update a single user's username.
     * @param id The ID of the user whose username should be changed
     * @param newUsername The new username
     * @return A void task
     */
    public Task<Void> updateUsername(String id, String newUsername) {
        return getCollection()
                .document(id)
                .update("username", newUsername, "username_lc", newUsername.toLowerCase());
    }

    /**
     * Asynchronously update a single user's email address.
     * @param id The ID of the user whose email address should be changed
     * @param newEmail The new email address
     * @return A void task
     */
    public Task<Void> updateEmail(String id, String newEmail) {
        return getCollection()
                .document(id)
                .update("email", newEmail, "email_lc", newEmail.toLowerCase());
    }

    /**
     * Asynchronously update a single user's display name
     * @param id The ID of the user whose display name should be changed
     * @param newDisplayName The new display name
     * @return A void task
     */
    public Task<Void> updateDisplayName(String id, String newDisplayName) {
        return getCollection()
                .document(id)
                .update("displayName", newDisplayName, "displayName_lc", newDisplayName.toLowerCase());
    }

    /**
     * Asynchronously update a single user's biography
     * @param id The ID of the user whose biography should be changed
     * @param newBiograpy The new biography
     * @return A void task
     */
    public Task<Void> updateBiography(String id, String newBiograpy) {
        return getCollection()
                .document(id)
                .update("biography", newBiograpy);
    }

    /**
     * Asynchronously update a single user's birthday
     * @param id The ID of the user whose birthday should be changed
     * @param newBirthday The new birthday
     * @return A void task
     */
    public Task<Void> updateBirthday(String id, Date newBirthday) {
        return getCollection()
                .document(id)
                .update("birthday", newBirthday);
    }

    /**
     * Asynchronously update a single user's avatar. This should be retrieved from
     * {@link ImagesRepository#upload(Uri)} or {@link ImagesRepository#upload(File)}
     * @param id The ID of the user whose avatar should be changed
     * @param newAvatarPath The path of the new avatar, which should be used for
     * {@link ImagesRepository#getUri(String)} or {@link ImagesRepository#getStream(String)}
     * @return A void task
     */
    public Task<Void> updateAvatar(String id, String newAvatarPath) {
        return getCollection()
                .document(id)
                .update("avatar", newAvatarPath);
    }

    /**
     * Asynchronously add user B to user A's list of followed users, and add user A to user B's
     * list of followers.
     * @param followingUserId The user that is following.
     * @param followedUserId The user that is being followed.
     * @return A task containing a boolean. This boolean will be true if the request was successful,
     * false otherwise.
     */
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

    /**
     * Asynchronously remove user B from user A's list of followed users, and remove user A from
     * user B's list of followers.
     * @param unfollowingUserId The user that is requesting to unfollow someone
     * @param unfollowedUserId The user that is losing a follower
     * @return
     */
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

    /**
     * Asynchronously add a post to a user's list of liked posts.
     * @param userId The user that is adding a new post to their list of liked posts
     * @param postId The post to be added to the list of liked posts
     * @return A task containing a boolean. This boolean will be true if the request was successful,
     * false otherwise.
     */
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

    /**
     * Asynchronously remove a post from a user's list of liked posts
     * @param userId The user that is removing a post from their list of liked posts
     * @param postId The post to be removed from the user's list of liked posts
     * @return A task containing a boolean, which will be true if the request was successful,
     * false otherwise.
     */
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

    /**
     * Asynchronously determine whether or not a username is being used by another user.
     * @param username The username to check (case insensitive)
     * @return A task containing a boolean, which will be true if the username is available, false
     * if another user is using it.
     */
    public Task<Boolean> isUsernameAvailable(String username) {
        return getCollection().whereEqualTo("username", username).get().continueWith(task -> {
            if (task.isSuccessful()) {
                return task.getResult().isEmpty();
            }
            return false;
        });
    }

    /**
     * Asynchronously determine whether or not an email address is being used by another user.
     * @param email The email address to check
     * @return A task containing a boolean, which will be true if the email address is available,
     * false if another user is using it.
     */
    public Task<Boolean> isEmailAvailable(String email) {
        return getCollection().whereEqualTo("email", email).get().continueWith(task -> {
            if (task.isSuccessful()) {
                return task.getResult().isEmpty();
            }
            return false;
        });
    }

    /**
     * Asynchronously fetch a single user based on their ID.
     * @param id The ID of the user to fetch
     * @return A task containing a {@link User}, which will be null if either the ID doesn't
     * correspond to any user or if something went wrong with the request.
     */
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

    /**
     * Asynchronously fetch a single user based on their username.
     * @param username The username of the user to fetch
     * @return A task containing a user, which will be null if either the username isn't being
     * used by anyone or if something went wrong with the request.
     */
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

    /**
     * Asynchronously fetch a single user based on their email address.
     * @param email The email address of the user to fetch
     * @return A task containing a user, which will be null if either the email address isn't being
     * used by anyone or if something went wrong with the request.
     */
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

    /**
     * Used as an intermediary between a {@link User} and a document. Need to store some additional
     * or different fields that we don't want in {@link User}. Specifically:
     * <ul>
     *     <li><code>username_lc</code>: used for case-insensitive searching of usernames</li>
     *     <li><code>email_lc</code>: used for case-insensitive searching of email address</li>
     *     <li><code>displayName_lc</code>: used for case-insensitive searching of display names</li>
     * </ul>
     */
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

    /**
     * Converts an instance of {@link User} to a more database-friendly database object.
     * @param user The user to convert
     * @return A converted database object
     */
    private static UserDbo toDbo(User user) {
        return new UserDbo(user);
    }

    /**
     * Safely converts a single {@link DocumentSnapshot} to a single {@link User}
     * @param ds The document snapshot to convert
     * @return A convert document snapshot. Will be null if something went wrong with the
     * conversion.
     */
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

    /**
     * A static instance of this repository for ease of use
     */
    private static UsersRepository instance = null;

    /**
     * Retrieve a static, permanent, non-null instance of this repository for ease of use.
     * @return A permanent instance of this repository
     */
    public static UsersRepository getInstance() {
        if (instance == null) {
            instance = new UsersRepository(FirebaseFirestore.getInstance().collection("users"));
        }
        return instance;
    }

}
