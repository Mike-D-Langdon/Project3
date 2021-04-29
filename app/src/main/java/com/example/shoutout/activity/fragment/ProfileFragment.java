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

/**
 * Fragment displaying a user's profile information and a timeline of their most recent posts. The
 * specific information shown is the following:
 * <ul>
 *     <li>Avatar</li>
 *     <li>Display name</li>
 *     <li>Username</li>
 *     <li>Birthday (may be unset)</li>
 *     <li>Biography (may be empty)</li>
 *     <li>Join date</li>
 *     <li>Number of people this user follows</li>
 *     <li>Number of people following this user</li>
 *     <li>A follow/unfollow button (if you're not looking at your own profile)</li>
 *     <li>A small timeline showing the user's most recent posts</li>
 *     <li>A button allowing the timeline to expand to show earlier posts</li>
 * </ul>
 *
 * @author Corneilious Eanes, Michael Langdon
 * @since April 29, 2021
 */
public class ProfileFragment extends Fragment {

    /**
     * Logging tag
     */
    private static final String TAG = ProfileFragment.class.getSimpleName();
    /**
     * Argument ID of the user to view in the fragment
     */
    private static final String ARG_VIEWED_USER = "viewedUser";
    /**
     * Argument ID of the logged-in local user
     */
    private static final String ARG_LOGGED_IN_USER = "loggedInUser";

    /**
     * Default limit used when pulling posts from the database server
     */
    private static final int LIMIT = 20;

    /**
     * The user whose information is displayed in this fragment
     */
    private User viewedUser;
    /**
     * The local, logged-in user. May or may not also refer to the {@link #viewedUser}
     */
    private User loggedInUser;
    /**
     * A list of posts currently displayed in the user profile's timeline
     */
    private List<Post> timeline;

    /**
     * Image view showing the user's avatar
     */
    private ImageView img_avatar;
    /**
     * Text view showing the user's display name
     */
    private TextView text_displayName;
    /**
     * Text view showing the user's username
     */
    private TextView text_username;
    /**
     * Text view showing the user's birthday
     */
    private TextView text_birthday;
    /**
     * Text view showing the date in which the user first signed up
     */
    private TextView text_joined;
    /**
     * Text view showing the user's (optional) biography
     */
    private TextView text_biography;
    /**
     * Text view showing the number of people this user follows
     */
    private TextView text_following;
    /**
     * Text view showing the number of people following this user
     */
    private TextView text_followers;
    /**
     * Button allowing the local user to follow/unfollow the displayed user
     */
    private Button btn_follow;
    /**
     * Text view informing the end user that the viewed user has uploaded no posts
     */
    private TextView text_noPosts;
    /**
     * Vertical linear layout acting as the timeline of the viewed user's posts
     */
    private LinearLayout layout_timeline;
    /**
     * Button allowing the end user to show more earlier posts of the viewed user
     */
    private Button btn_morePosts;
    /**
     * Listener for determining whether or not a post has already been liked by the local user
     */
    private PostFragment.IsPostLikedListener isPostLikedListener;

    public ProfileFragment() {
    }

    /**
     * Creates a new instance of this fragment.
     * @param viewedUser The user whose information will be displayed in this fragment
     * @param loggedInUser The user that is currently logged in and using this app
     * @return A new instance of this fragment
     * @see ProfileFragment
     */
    public static ProfileFragment newInstance(User viewedUser, User loggedInUser) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_VIEWED_USER, viewedUser);
        args.putSerializable(ARG_LOGGED_IN_USER, loggedInUser);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Set the listener for when this fragment needs to check if a post in this user's timeline
     * has already been liked by the local user. <strong>Important</strong>: This should be set
     * before the fragment is attached to the parent activity.
     * @param listener A listener to check if a post from this user has been liked by the local user
     */
    public void setIsPostLikedListener(PostFragment.IsPostLikedListener listener) {
        isPostLikedListener = listener;
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

            // download and fit viewed user's avatar
            ImagesRepository.getInstance().getUri(viewedUser.getAvatarUri()).addOnSuccessListener(avatarUri -> {
                Picasso.get().load(avatarUri).fit().centerInside().into(img_avatar);
            });
            text_displayName.setText(viewedUser.getDisplayName());
            text_username.setText(getResources().getString(R.string.username_format, viewedUser.getUsername()));
            // if the birthday is not set, don't show the text view at all
            if (viewedUser.getBirthday() != null) {
                text_birthday.setText(DateTimeUtil.formatLongerDate(viewedUser.getBirthday()));
            } else {
                text_birthday.setVisibility(View.INVISIBLE);
            }
            text_joined.setText(getResources().getString(R.string.profile_joined, DateTimeUtil.formatLongerDate(viewedUser.getCreated())));
            text_biography.setText(viewedUser.getBiography());
            text_following.setText(getResources().getString(R.string.profile_following, viewedUser.getFollowing().size()));
            text_followers.setText(getResources().getString(R.string.profile_followers, viewedUser.getFollowers().size()));

            // immediately queue up a bunch of posts to be loaded
            loadMorePosts(LIMIT, new Date());

            // show the follow button only if the viewed user and the local user are different users
            if (viewedUser.getId().equals(loggedInUser.getId())) {
                btn_follow.setVisibility(View.GONE);
            } else {
                // Display either a "Follow" or "Unfollow" button depending on whether or not the
                // viewed user is followed by the local user
                if (viewedUser.getFollowers().contains(loggedInUser.getId())) {
                    btn_follow.setText(getResources().getString(R.string.unfollow));
                    btn_follow.setBackgroundColor(Color.RED);
                } else {
                    btn_follow.setText(getResources().getString(R.string.follow));
                    btn_follow.setBackgroundColor(Color.GREEN);
                }
            }

           btn_follow.setOnClickListener(v -> {
               // need to both update the followers list of the viewed user and the following list
               // of the local user. depending on whether or not the local user follows the viewed
               // user when this button is hit depends on what is actually done.
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
                // figure out the date of the last post in the timeline, then fetch all posts
                // that were posted before that, since they're all listed in order by post date
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
                // this could be empty if 1) the viewed user hasn't posted anything, or 2) the last
                // request reached the end of the user's post history. this can be differentiated
                // by if the master posts list is empty or not.
                List<Post> posts = postsTask.getResult();
                Log.d(TAG, "Retrieved " + posts.size() + " posts from user " + viewedUser.getId());
                if (!posts.isEmpty()) {
                    if (timeline.isEmpty()) {
                        // if posts were found and there currently aren't any in the timeline,
                        // then "remove" the text view that says there aren't any posts
                        text_noPosts.setVisibility(View.GONE);
                    }
                    if (posts.size() < limit) {
                        // remove the more posts button when we've reached the end of the viewer
                        // user's post history
                        btn_morePosts.setVisibility(View.INVISIBLE);
                    }
                    timeline.addAll(posts);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    for (Post post : posts) {
                        // remember to set the post liked listener before adding it
                        PostFragment frag = PostFragment.newInstance(viewedUser, post);
                        frag.setIsPostLikedListener(isPostLikedListener);
                        transaction.add(R.id.layout_profile_timeline, frag).addToBackStack(null);
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