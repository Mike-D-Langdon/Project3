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

public class PostFragment extends Fragment {

    private static final String TAG = PostFragment.class.getSimpleName();
    private static final String
            ARG_USER = "user",
            ARG_POST = "post";

    private User user;
    private Post post;
    private UserSearchResultFragment.OnProfileClickListener onProfileClick;
    private AtomicInteger likeCountRef;
    private AtomicBoolean processingLike;

    private ImageView image_avatar;
    private TextView text_displayName;
    private TextView text_username;
    private TextView text_postBody;
    private ImageView image_postImage;
    private TextView text_posted;
    private TextView text_likeCount;
    private ImageButton button_like;
    private IsPostLikedListener isPostLikedListener;

    public PostFragment() {
    }

    public static PostFragment newInstance(User user, Post post) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        args.putSerializable(ARG_POST, post);
        fragment.setArguments(args);
        return fragment;
    }

    public void setIsPostLikedListener(IsPostLikedListener listener) {
        isPostLikedListener = listener;
    }

    public void setOnProfileClickListener(UserSearchResultFragment.OnProfileClickListener listener) {
        this.onProfileClick = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        likeCountRef = new AtomicInteger(0);
        processingLike = new AtomicBoolean(false);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(ARG_USER);
            post = (Post) getArguments().getSerializable(ARG_POST);
            likeCountRef.set(post.getLikes());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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

            ImagesRepository.getInstance().getUri(user.getAvatarUri()).addOnSuccessListener(uri -> {
                Picasso.get().load(uri).fit().centerCrop().into(image_avatar);
            });
            image_avatar.setOnClickListener(v -> {
                onProfileClick.handle(user);
            });
            text_displayName.setText(user.getDisplayName());
            text_username.setText(getResources().getString(R.string.username_format, user.getUsername()));
            text_postBody.setText(post.getText());
            if (post.getImages() != null && !post.getImages().isEmpty()) {
                ImagesRepository.getInstance().getUri(post.getImages().get(0)).addOnSuccessListener(uri -> {
                    Picasso.get().load(uri).into(image_postImage);
                });
            } else {
                image_postImage.setVisibility(View.GONE);
            }
            text_posted.setText(DateTimeUtil.formatLongerDate(post.getPosted()));
            text_likeCount.setText(String.format(Locale.ENGLISH, "%d", post.getLikes()));

            if (isPostLikedListener != null) {
                isPostLikedListener.handle(post.getId()).addOnSuccessListener(liked -> {
                    if (liked) {
                        button_like.setImageDrawable(getResources().getDrawable(R.drawable.ic_like2));
                    }
                });
            }
            button_like.setOnClickListener(v -> {
                if (processingLike.get()) {
                    return;
                }
                if (isPostLikedListener != null) {
                    processingLike.set(true);
                    isPostLikedListener.handle(post.getId()).addOnSuccessListener(liked -> {
                        if (liked) {
                            Task<Boolean> likePostTask = PostsRepository.getInstance().removeLike(post.getId());
                            Task<Boolean> userLikeTask = UsersRepository.getInstance().unlike(FirebaseAuth.getInstance().getUid(), post.getId());
                            Tasks.whenAllComplete(likePostTask, userLikeTask).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, String.format("Post (%s) unliked", post.getId()));
                                } else {
                                    Log.w(TAG, "Could not unlike post", task.getException());
                                }
                                processingLike.set(false);
                            });
                            button_like.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
                            text_likeCount.setText(String.format(Locale.ENGLISH, "%d", likeCountRef.decrementAndGet()));
                        } else {
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

    public interface IsPostLikedListener {
        Task<Boolean> handle(String postId);
    }

}