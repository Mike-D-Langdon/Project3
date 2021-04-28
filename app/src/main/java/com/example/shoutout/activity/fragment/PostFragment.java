package com.example.shoutout.activity.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.shoutout.R;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class PostFragment extends Fragment {

    private static final String
            ARG_AVATAR_URL = "avatarUrl",
            ARG_DISPLAY_NAME = "displayName",
            ARG_USERNAME = "username",
            ARG_BODY = "body",
            ARG_GALLERY_PREVIEW_URL = "galleryPreviewUrl",
            ARG_GALLERY_COUNT = "galleryCount",
            ARG_LIKE_COUNT = "likeCount",
            ARG_COMMENT_COUNT = "commentCount";

    private String avatarUrl;
    private String displayName;
    private String username;
    private String body;
    private String galleryPreviewUrl;
    private int galleryCount;
    private int likeCount;
    private int commentCount;

    public PostFragment() {
    }

    public static PostFragment newInstance(String avatarUrl, String displayName, String username, String body, String galleryPreviewUrl, int galleryCount, int likeCount, int commentCount) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_AVATAR_URL, avatarUrl);
        args.putString(ARG_DISPLAY_NAME, displayName);
        args.putString(ARG_USERNAME, username);
        args.putString(ARG_BODY, body);
        args.putString(ARG_GALLERY_PREVIEW_URL, galleryPreviewUrl);
        args.putInt(ARG_GALLERY_COUNT, galleryCount);
        args.putInt(ARG_LIKE_COUNT, likeCount);
        args.putInt(ARG_COMMENT_COUNT, commentCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            avatarUrl = getArguments().getString(ARG_AVATAR_URL);
            displayName = getArguments().getString(ARG_DISPLAY_NAME);
            username = getArguments().getString(ARG_USERNAME);
            body = getArguments().getString(ARG_BODY);
            galleryPreviewUrl = getArguments().getString(ARG_GALLERY_PREVIEW_URL);
            galleryCount = getArguments().getInt(ARG_GALLERY_COUNT);
            likeCount = getArguments().getInt(ARG_LIKE_COUNT);
            commentCount = getArguments().getInt(ARG_COMMENT_COUNT);
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
            Picasso.get().load(Uri.parse(avatarUrl)).fit().centerCrop().into(getView().<ImageView>findViewById(R.id.image_post_avatar));
            getView().<TextView>findViewById(R.id.text_post_displayName).setText(displayName);
            getView().<TextView>findViewById(R.id.text_post_username).setText("@" + username);
            getView().<TextView>findViewById(R.id.text_post_body).setText(body);
            Picasso.get().load(Uri.parse(galleryPreviewUrl)).fit().centerCrop().into(getView().<ImageView>findViewById(R.id.image_post_galleryPreview));
            getView().<TextView>findViewById(R.id.text_post_galleryCount).setText(String.format(Locale.ENGLISH, "%d", galleryCount));
            getView().<TextView>findViewById(R.id.text_post_likeCount).setText(String.format(Locale.ENGLISH, "%d", likeCount));
            getView().<TextView>findViewById(R.id.text_post_commentCount).setText(String.format(Locale.ENGLISH, "%d", commentCount));
        }
    }

}