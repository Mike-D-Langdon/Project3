package com.example.shoutout.activity.fragment;

import android.os.Bundle;
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
import com.example.shoutout.dbo.Post;
import com.example.shoutout.dbo.User;
import com.example.shoutout.util.DateTimeUtil;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class PostFragment extends Fragment {

    private static final String TAG = PostFragment.class.getSimpleName();
    private static final String
            ARG_USER = "user",
            ARG_POST = "post";

    private User user;
    private Post post;
    private UserSearchResultFragment.OnProfileClickListener listener;

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

    public void setOnProfileClickListener(UserSearchResultFragment.OnProfileClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(ARG_USER);
            post = (Post) getArguments().getSerializable(ARG_POST);
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
            ImagesRepository.getInstance().getUri(user.getAvatarUri()).addOnSuccessListener(uri -> {
                Picasso.get().load(uri).fit().centerCrop().into(getView().<ImageView>findViewById(R.id.image_post_avatar));
            });
            getView().<ImageButton>findViewById(R.id.image_post_avatar).setOnClickListener(v -> {
                listener.handle(user);
            });
            getView().<TextView>findViewById(R.id.text_post_displayName).setText(user.getDisplayName());
            getView().<TextView>findViewById(R.id.text_post_username).setText(getResources().getString(R.string.username_format, user.getUsername()));
            getView().<TextView>findViewById(R.id.text_post_body).setText(post.getText());
            if (post.getImages() != null && !post.getImages().isEmpty()) {
                ImagesRepository.getInstance().getUri(post.getImages().get(0)).addOnSuccessListener(uri -> {
                    Picasso.get().load(uri).into(getView().<ImageView>findViewById(R.id.image_post_galleryPreview));
                });
            } else {
                getView().findViewById(R.id.image_post_galleryPreview).setVisibility(View.GONE);
            }
            getView().<TextView>findViewById(R.id.text_post_posted).setText(DateTimeUtil.formatLongerDate(post.getPosted()));
            getView().<TextView>findViewById(R.id.text_post_galleryCount).setText(String.format(Locale.ENGLISH, "%d", 1));
            getView().<TextView>findViewById(R.id.text_post_likeCount).setText(String.format(Locale.ENGLISH, "%d", post.getLikes()));
            getView().<TextView>findViewById(R.id.text_post_commentCount).setText(String.format(Locale.ENGLISH, "%d", post.getComments()));
        }
    }

}