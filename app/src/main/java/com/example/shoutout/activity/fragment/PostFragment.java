package com.example.shoutout.activity.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.shoutout.R;
import com.example.shoutout.db.ImagesRepository;
import com.example.shoutout.db.PostsRepository;
import com.example.shoutout.db.UsersRepository;
import com.example.shoutout.dbo.Post;
import com.example.shoutout.dbo.User;
import com.example.shoutout.util.DateTimeUtil;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Fragment showcasing a post. The information shown includes all of the following:
 * <ul>
 *     <li>User's avatar</li>
 *     <li>User's display name</li>
 *     <li>User's username</li>
 *     <li>Text contents (optional)</li>
 *     <li>Image (optional)</li>
 *     <li>Date posted</li>
 *     <li>Number of likes</li>
 * </ul>
 *
 * @author Corneilious Eanes
 * @since April 29, 2021
 */
public class PostFragment extends Fragment {

    /**
     * Logging tag
     */
    private static final String TAG = PostFragment.class.getSimpleName();
    /**
     * Argument ID of the post's user
     */
    private static final String ARG_USER = "user";
    /**
     * Argument ID of the post itself
     */
    private static final String ARG_POST = "post";

    /**
     * An instance of the user that uploaded the post
     */
    private User user;
    /**
     * The post to be featured in this fragment
     */
    private Post post;
    /**
     * Listener for handling when the user clicks on a post's profile avatar
     */
    private UserSearchResultFragment.OnProfileClickListener onProfileClick;
    /**
     * Reference used to change the like counter (for display purposes only)
     */
    private AtomicInteger likeCountRef;
    /**
     * Reference used to keep track of the state between when the user clicks the like button
     * and when the server has fully processed the like/unlike request
     */
    private AtomicBoolean processingLike;

    /**
     * The image view of the avatar of the user that uploaded this post
     */
    private ImageView image_avatar;
    /**
     * The text view of the user's display name
     */
    private TextView text_displayName;
    /**
     * The text view of the user's username
     */
    private TextView text_username;
    /**
     * The text view of the post's text contents (may be empty)
     */
    private TextView text_postBody;
    /**
     * The image view of the post's attached image (may be empty)
     */
    private ImageView image_postImage;
    /**
     * The text view of the date of which this post was uploaded
     */
    private TextView text_posted;
    /**
     * The text view showing the number of times the post has been liked by other users
     */
    private TextView text_likeCount;
    /**
     * The button allowing the local user to like/unlike this post
     */
    private ImageButton button_like;
    /**
     * A listener for when this fragment needs to figure out if the local user has already liked
     * this post
     */
    private IsPostLikedListener isPostLikedListener;

    public PostFragment() {
    }

    /**
     * Get the post instance used by this fragment
     * @return Post instance used by this fragment
     */
    public Post getPost() {
        return post;
    }

    /**
     * Creates a new instance of this fragment.
     * @param user An instance of the user who uploaded the post
     * @param post The post itself
     * @return A new instance of this fragment
     * @see PostFragment
     */
    public static PostFragment newInstance(User user, Post post) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        args.putSerializable(ARG_POST, post);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Update the listener that checks if this post should be liked. <strong>Important</strong>:
     * This should be set before the fragment is attached to the parent activity.
     * @param listener A listener that checks if this post should be liked
     */
    public void setIsPostLikedListener(IsPostLikedListener listener) {
        isPostLikedListener = listener;
    }

    /**
     * Update the listener that is fired when the user clicks on the post's user's avatar.
     * <strong>Important</strong>: This should be set before the fragment is attached to the parent
     * activity.
     * @param listener A listener that is fired when the user clicks on the post's user's avatar
     */
    public void setOnProfileClickListener(UserSearchResultFragment.OnProfileClickListener listener) {
        this.onProfileClick = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // make sure to initialize these
        likeCountRef = new AtomicInteger(0);
        processingLike = new AtomicBoolean(false);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(ARG_USER);
            post = (Post) getArguments().getSerializable(ARG_POST);
            // this will be used when the user taps the like button
            likeCountRef.set(post.getLikes());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getView() != null) {

            image_avatar = getView().findViewById(R.id.image_post_avatar);
            text_displayName = getView().findViewById(R.id.text_post_displayName);
            text_username = getView().findViewById(R.id.text_post_username);
            text_postBody = getView().findViewById(R.id.text_post_body);
            image_postImage = getView().findViewById(R.id.image_post_galleryPreview);
            text_posted = getView().findViewById(R.id.text_post_posted);
            text_likeCount = getView().findViewById(R.id.text_post_likeCount);
            button_like = getView().findViewById(R.id.button_post_like);

            // download and fit the user's avatar
            ImagesRepository.getInstance().getUri(user.getAvatarUri()).addOnSuccessListener(uri -> {
                Picasso.get().load(uri).fit().centerCrop().into(image_avatar);
            });
            // here's where the profile click listener is used. meant to contain a function that is
            // ultimately supplied from the MainActivity
            image_avatar.setOnClickListener(v -> {
                onProfileClick.handle(user);
            });
            text_displayName.setText(user.getDisplayName());
            text_username.setText(getResources().getString(R.string.username_format, user.getUsername()));
            text_postBody.setText(post.getText());
            // if there is an image attached to this post, then download and fit it
            // otherwise, "remove" it from the fragment
            if (post.getImages() != null && !post.getImages().isEmpty()) {
                ImagesRepository.getInstance().getUri(post.getImages().get(0)).addOnSuccessListener(uri -> {
                    Picasso.get().load(uri).into(image_postImage);
                });
            } else {
                // don't actually remove it
                image_postImage.setVisibility(View.GONE);
            }
            text_posted.setText(DateTimeUtil.formatLongerDate(post.getPosted()));
            text_likeCount.setText(String.format(Locale.ENGLISH, "%d", post.getLikes()));

            if (isPostLikedListener != null) {
                isPostLikedListener.handle(post.getId()).addOnSuccessListener(liked -> {
                    // set the icon as already being liked if the local user already liked this post
                    if (liked) {
                        button_like.setImageDrawable(getResources().getDrawable(R.drawable.ic_like2));
                    }
                });
            }

            button_like.setOnClickListener(v -> {
                // prevents the user from spamming the like button and messing things up
                if (processingLike.get()) {
                    return;
                }
                if (isPostLikedListener != null) {
                    // this way, the following code is only ever run when the server is not handling
                    // any like/unlike requests
                    processingLike.set(true);
                    isPostLikedListener.handle(post.getId()).addOnSuccessListener(liked -> {
                        // unlike the post if it's already liked, otherwise, like it
                        // (kinda confusing, yeah)
                        if (liked) {
                            // need to update both the users and posts repositories
                            Task<Boolean> likePostTask = PostsRepository.getInstance().removeLike(post.getId());
                            Task<Boolean> userLikeTask = UsersRepository.getInstance().unlike(FirebaseAuth.getInstance().getUid(), post.getId());
                            Tasks.whenAllComplete(likePostTask, userLikeTask).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, String.format("Post (%s) unliked", post.getId()));
                                } else {
                                    Log.w(TAG, "Could not unlike post", task.getException());
                                }
                                // only allow clicking the button again when the server has finished
                                // processing both requests
                                processingLike.set(false);
                            });
                            // although the server is not yet done processing the requests, we
                            // should still update the icon and number to give the illusion that
                            // the transaction was much faster than it actually is
                            button_like.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
                            text_likeCount.setText(String.format(Locale.ENGLISH, "%d", likeCountRef.decrementAndGet()));
                        } else {
                            // refer to the comments above
                            Task<Boolean> likePostTask = PostsRepository.getInstance().addLike(post.getId());
                            Task<Boolean> userLikeTask = UsersRepository.getInstance().like(FirebaseAuth.getInstance().getUid(), post.getId());
                            Tasks.whenAllComplete(likePostTask, userLikeTask).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, String.format("Post (%s) liked", post.getId()));
                                } else {
                                    Log.w(TAG, "Could not like post", task.getException());
                                }
                                processingLike.set(false);
                            });
                            button_like.setImageDrawable(getResources().getDrawable(R.drawable.ic_like2));
                            text_likeCount.setText(String.format(Locale.ENGLISH, "%d", likeCountRef.incrementAndGet()));
                        }
                    });
                }
            });
        }
    }

    /**
     * Asynchronous listener for checking whether or not a post has already been liked
     */
    public interface IsPostLikedListener {
        /**
         * @param postId The ID of the post to check
         * @return A task containing a boolean. Should evaluate to true if the post is already
         * liked by the local user, false otherwise
         */
        Task<Boolean> handle(String postId);
    }

}