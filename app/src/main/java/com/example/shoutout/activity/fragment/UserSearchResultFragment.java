package com.example.shoutout.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.shoutout.R;
import com.example.shoutout.db.ImagesRepository;
import com.example.shoutout.dbo.User;
import com.squareup.picasso.Picasso;

public class UserSearchResultFragment extends Fragment {

    private static final String ARG_USER = "user";

    public UserSearchResultFragment() {
    }

    private User user;
    private OnProfileClickListener listener;

    private ConstraintLayout layout;
    private ImageView img_avatar;
    private TextView txt_displayName;
    private TextView txt_username;

    public static UserSearchResultFragment newInstance(User user) {
        UserSearchResultFragment fragment = new UserSearchResultFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(ARG_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_search_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getView() != null) {
            layout = getView().findViewById(R.id.layout_userSearchResult_parent);
            img_avatar = getView().findViewById(R.id.image_userSearchResult_avatar);
            txt_displayName = getView().findViewById(R.id.text_userSearchResult_displayName);
            txt_username = getView().findViewById(R.id.text_userSearchResult_username);

            layout.setOnClickListener(v -> listener.handle(user));
            ImagesRepository.getInstance().getUri(user.getAvatarUri()).addOnSuccessListener(uri -> {
               Picasso.get().load(uri).into(img_avatar);
            });
            txt_displayName.setText(user.getDisplayName());
            txt_username.setText(getResources().getString(R.string.username_format, user.getUsername()));
        }
    }

    public void setOnProfileClickListener(OnProfileClickListener listener) {
        this.listener = listener;
    }

    public interface OnProfileClickListener {
        void handle(User user);
    }

}