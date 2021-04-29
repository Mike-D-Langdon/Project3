package com.example.shoutout.activity.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.shoutout.R;
import com.example.shoutout.db.ImagesRepository;
import com.example.shoutout.db.PostsRepository;
import com.example.shoutout.db.UsersRepository;
import com.example.shoutout.dbo.Post;
import com.example.shoutout.dbo.User;
import com.example.shoutout.util.DateTimeUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();
    private static final String
            ARG_VIEWED_USER = "viewedUser",
            ARG_LOGGED_IN_USER = "loggedInUser";
    private static final int LIMIT = 20;

    private User viewedUser;
    private User loggedInUser;
    private List<Post> timeline;

    private ImageView img_avatar;
    private TextView text_displayName;
    private TextView text_username;
    private TextView text_birthday;
    private TextView text_joined;
    private TextView text_biography;
    private TextView text_following;
    private TextView text_followers;
    private Button btn_follow;
    private TextView text_noPosts;
    private LinearLayout layout_timeline;
    private Button btn_morePosts;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance(User viewedUser, User loggedInUser) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_VIEWED_USER, viewedUser);
        args.putSerializable(ARG_LOGGED_IN_USER, loggedInUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timeline = new ArrayList<>(LIMIT);
        if (getArguments() != null) {
            viewedUser = (User) getArguments().getSerializable(ARG_VIEWED_USER);
            loggedInUser = (User) getArguments().getSerializable(ARG_LOGGED_IN_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getView() != null) {
            img_avatar = getView().findViewById(R.id.image_profile_avatar2);
            text_displayName = getView().findViewById(R.id.text_profile_displayName2);
            text_username = getView().findViewById(R.id.text_profile_username2);
            text_birthday = getView().findViewById(R.id.text_profile_birthday2);
            text_joined = getView().findViewById(R.id.text_profile_joined);
            text_biography = getView().findViewById(R.id.text_profile_biography2);
            text_following = getView().findViewById(R.id.text_profile_followingCount);
            text_followers = getView().findViewById(R.id.text_profile_followerCount);
            btn_follow = getView().findViewById(R.id.button_profile_follow);
            text_noPosts = getView().findViewById(R.id.text_profile_noPosts);
            layout_timeline = getView().findViewById(R.id.layout_profile_timeline);
            btn_morePosts = getView().findViewById(R.id.button_profile_morePosts);

            ImagesRepository.getInstance().getUri(viewedUser.getAvatarUri()).addOnSuccessListener(avatarUri -> {
                Picasso.get().load(avatarUri).fit().centerInside().into(img_avatar);
            });
            text_displayName.setText(viewedUser.getDisplayName());
            text_username.setText(getResources().getString(R.string.username_format, viewedUser.getUsername()));
            if (viewedUser.getBirthday() != null) {
                text_birthday.setText(DateTimeUtil.formatLongerDate(viewedUser.getBirthday()));
            } else {
                text_birthday.setVisibility(View.INVISIBLE);
            }
            text_joined.setText(getResources().getString(R.string.profile_joined, DateTimeUtil.formatLongerDate(viewedUser.getCreated())));
            text_biography.setText(viewedUser.getBiography());
            text_following.setText(getResources().getString(R.string.profile_following, viewedUser.getFollowing().size()));
            text_followers.setText(getResources().getString(R.string.profile_followers, viewedUser.getFollowers().size()));

            loadMorePosts(LIMIT, new Date());

            if (viewedUser.getId().equals(loggedInUser.getId())) {
                btn_follow.setVisibility(View.GONE);
            } else {
                if (viewedUser.getFollowers().contains(loggedInUser.getId())) {
                    btn_follow.setText(getResources().getString(R.string.unfollow));
                    btn_follow.setBackgroundColor(Color.RED);
                } else {
                    btn_follow.setText(getResources().getString(R.string.follow));
                    btn_follow.setBackgroundColor(Color.GREEN);
                }
            }

           btn_follow.setOnClickListener(v -> {
               if (viewedUser.getFollowers().contains(loggedInUser.getId())) {
                   UsersRepository.getInstance().unfollow(loggedInUser.getId(), viewedUser.getId()).addOnCompleteListener(task -> {
                      if (task.isSuccessful()) {
                          Log.i(TAG, "Unfollowed user " + viewedUser.getId());
                          loggedInUser.getFollowing().remove(viewedUser.getId());
                          viewedUser.getFollowers().remove(loggedInUser.getId());
                          btn_follow.setText(getResources().getString(R.string.follow));
                          btn_follow.setBackgroundColor(Color.GREEN);
                          text_followers.setText(getResources().getString(R.string.profile_followers, viewedUser.getFollowers().size()));
                      } else {
                          Log.w(TAG, "Could not unfollow user", task.getException());
                      }
                   });
               } else {
                   UsersRepository.getInstance().follow(loggedInUser.getId(), viewedUser.getId()).addOnCompleteListener(task -> {
                       if (task.isSuccessful()) {
                           Log.i(TAG, "Followed user " + viewedUser.getId());
                           loggedInUser.getFollowing().add(viewedUser.getId());
                           viewedUser.getFollowers().add(loggedInUser.getId());
                           btn_follow.setText(getResources().getString(R.string.unfollow));
                           btn_follow.setBackgroundColor(Color.RED);
                           text_followers.setText(getResources().getString(R.string.profile_followers, viewedUser.getFollowers().size()));
                       } else {
                           Log.w(TAG, "Could not follow user", task.getException());
                       }
                   });
               }
           });

            btn_morePosts.setOnClickListener(v -> {
                Date before;
                if (timeline.isEmpty()) {
                    before = new Date();
                } else {
                    before = timeline.get(timeline.size() - 1).getPosted();
                }
                loadMorePosts(LIMIT, before);
            });
        }
    }

    private void loadMorePosts(int limit, Date before) {
        PostsRepository.getInstance().getPostsFromUser(viewedUser.getId(), limit, before).addOnCompleteListener(postsTask -> {
            if (postsTask.isSuccessful()) {
                List<Post> posts = postsTask.getResult();
                Log.d(TAG, "Retrieved " + posts.size() + " posts from user " + viewedUser.getId());
                if (!posts.isEmpty()) {
                    if (timeline.isEmpty()) {
                        text_noPosts.setVisibility(View.GONE);
                    }
                    if (posts.size() < limit) {
                        btn_morePosts.setVisibility(View.INVISIBLE);
                    }
                    timeline.addAll(posts);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    for (Post post : posts) {
                        transaction.add(R.id.layout_profile_timeline, PostFragment.newInstance(
                                viewedUser, post
                        )).addToBackStack(null);
                    }
                    transaction.commit();
                } else {
                    btn_morePosts.setVisibility(View.INVISIBLE);
                }
            } else {
                Log.w(TAG, "Could not retrieve user posts", postsTask.getException());
            }
        });
    }

}