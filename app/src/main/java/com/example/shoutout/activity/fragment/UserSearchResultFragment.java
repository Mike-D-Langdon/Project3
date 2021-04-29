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

/**
 * Small fragment that shows a user-based result from a search. The information displayed consists
 * of the following:
 * <ul>
 *     <li>Avatar</li>
 *     <li>Display name</li>
 *     <li>Username</li>
 * </ul>
 *
 * @author Corneilious Eanes
 * @since April 29, 2021
 */
public class UserSearchResultFragment extends Fragment {

    /**
     * Logging tag
     */
    private static final String ARG_USER = "user";

    public UserSearchResultFragment() {
    }

    /**
     * The user displayed in this fragment
     */
    private User user;
    /**
     * Listener that is fired when the end user clicks on this fragment
     */
    private OnProfileClickListener listener;

    /**
     * Layout that actually stores and handles the profile click listener
     */
    private ConstraintLayout layout;
    /**
     * Image view that displays the user's avatar
     */
    private ImageView img_avatar;
    /**
     * Text view that displays the user's display name
     */
    private TextView txt_displayName;
    /**
     * Text view that displays the user's username
     */
    private TextView txt_username;

    /**
     * Create a new instance of this fragment.
     * @param user The user to be portrayed in this fragment
     * @return A new instance of this fragment
     * @see UserSearchResultFragment
     */
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

            // make it so if the user clicks anywhere on this fragment, the listener is fired
            layout.setOnClickListener(v -> listener.handle(user));
            ImagesRepository.getInstance().getUri(user.getAvatarUri()).addOnSuccessListener(uri -> {
               Picasso.get().load(uri).into(img_avatar);
            });
            txt_displayName.setText(user.getDisplayName());
            txt_username.setText(getResources().getString(R.string.username_format, user.getUsername()));
        }
    }

    /**
     * Update the listener that is fired when the user clicks anywhere on this fragment
     * @param listener A listener that is fired when the user clicks anywhere on this fragment
     */
    public void setOnProfileClickListener(OnProfileClickListener listener) {
        this.listener = listener;
    }

    /**
     * Listener that handles when the end user clicks on a user's profile
     */
    public interface OnProfileClickListener {
        /**
         * Handle the request
         * @param user The user corresponding to what the end user clicked on
         */
        void handle(User user);
    }

}