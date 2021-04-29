package com.example.shoutout.activity.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.shoutout.R;
import com.example.shoutout.db.PostsRepository;
import com.example.shoutout.db.UsersRepository;
import com.example.shoutout.dbo.Post;
import com.example.shoutout.dbo.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Fragment that displays a collection of posts based on who the local logged-in user follows.
 * Everything is displayed from most recent at the top to earliest at the bottom.
 *
 * @author Corneilious Eanes
 * @since April 29, 2021
 */
public class TimelineFragment extends Fragment {
    // a lot of this works identically to ProfileFragment. probably a better idea to integrate this
    // into ProfileFragment instead, but eh whatever

    /**
     * Logging tag
     */
    private static final String TAG = TimelineFragment.class.getSimpleName();

    public TimelineFragment() {
    }

    /**
     * Reference to the local logged-in user
     */
    private AtomicReference<User> localUserRef;
    /**
     * Allows results of user information requests to be cached, allowing for much quicker load
     * times.
     * @see #getUserAsync(String)
     */
    private Map<String, User> usersPool;
    /**
     * List of posts currently stored in this timeline. The 0th post will be the earliest, and the
     * (n-1)th post will be the latest.
     */
    private List<Post> posts;
    /**
     * Listener that is fired when a post's user's avatar is clicked on, directing the end user
     * to the post's user's profile
     */
    private UserSearchResultFragment.OnProfileClickListener onProfileClickListener;

    /**
     * Linear layout that contains all the instances of {@link PostFragment}
     */
    private LinearLayout layout_posts;
    /**
     * Button that allows more posts to be displayed when the end of the timeline has been reached
     */
    private Button button_morePosts;
    /**
     * Text view that displays if the user is not following anyone
     */
    private TextView text_notFollowing;
    /**
     * Text view that displays if all users that the user is following haven't made any posts
     */
    private TextView text_noPosts;
    /**
     * Text view that displays while the fragment is fetching more posts from the server
     */
    private TextView text_loading;
    /**
     * Listener that is called when the fragment needs to know if the local user has liked a
     * specific post
     */
    private PostFragment.IsPostLikedListener isPostLikedListener;

    /**
     * Create a new instance of this fragment.
     * @return A new instance of this fragment
     */
    public static TimelineFragment newInstance() {
        TimelineFragment fragment = new TimelineFragment();
        // no arguments required
        return fragment;
    }

    /**
     * Update the listener that is fired when a post's user's avatar is clicked
     * @param listener A listener that is fired when a post's user's avatar is clicked
     */
    public void setOnProfileClickListener(UserSearchResultFragment.OnProfileClickListener listener) {
        onProfileClickListener = listener;
    }

    /**
     * Update the listener that is called when the fragment needs to know if the local user has
     * liked a specific post
     * @param listener A listener that is called when the fragment needs to know if the local user
     *                 has liked a specific post
     */
    public void setIsPostLikedListener(PostFragment.IsPostLikedListener listener) {
        isPostLikedListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usersPool = new HashMap<>();
        localUserRef = new AtomicReference<>();
        posts = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getView() != null) {
            layout_posts = getView().findViewById(R.id.layout_timeline_posts);
            button_morePosts = getView().findViewById(R.id.button_timeline_morePosts);
            text_notFollowing = getView().findViewById(R.id.text_timeline_notFollowing);
            text_noPosts = getView().findViewById(R.id.text_timeline_noPosts);
            text_loading = getView().findViewById(R.id.text_timeline_loading);

            button_morePosts.setOnClickListener(v -> {
                // find the earliest date in the timeline, then display posts uploaded before that
                Date before;
                if (posts.isEmpty()) {
                    before = new Date();
                } else {
                    before = posts.get(posts.size() - 1).getPosted();
                }
                loadMorePosts(30, before);
            });

            UsersRepository.getInstance().get(FirebaseAuth.getInstance().getUid()).addOnSuccessListener(user -> {
                // only update the timeline when the local user's information has been gathered
               localUserRef.set(user);
               // make sure to re-enable the morePosts button and "remove" the loading text view
               button_morePosts.setEnabled(true);
               text_loading.setVisibility(View.GONE);

               if (user.getFollowing().isEmpty()) {
                   // if the local user isn't following anyone, then display that
                   text_notFollowing.setVisibility(View.VISIBLE);
               } else {
                   // otherwise, load 30 posts from their following list
                   loadMorePosts(20, new Date());
               }
            });
        }
    }

    /**
     * Asynchronously fetch a user from the database server
     * @param id The ID of the user
     * @return A task returning a user, which will be null (the user, not the task) if the ID
     * doesn't correspond to a specific user
     */
    private Task<User> getUserAsync(String id) {
        // fun fact: there exists a method called Tasks.await(Task) that can't be called on the
        // main thread, so it's practically useless. thus why we have to use this weird async
        // approach and why the pool of users is required to prevent astronomically slow load times
        if (usersPool.containsKey(id)) {
            return Tasks.forResult(usersPool.get(id));
        }
        return UsersRepository.getInstance().get(id).addOnSuccessListener(user -> {
            if (user != null) {
                usersPool.put(user.getId(), user);
            }
        });
    }

    /**
     * Will send a request to the database server asking for posts uploaded by those present in the
     * local user's following list, and will update the timeline accordingly.
     * @param limit The maximum number of posts to fetch
     * @param before All fetched posts will have a post date before this
     */
    public void loadMorePosts(int limit, Date before) {
        final User localUser = localUserRef.get();
        if (localUser != null) {
            // disable the morePosts button while we do this
            button_morePosts.setEnabled(false);
            // also enable the loading text to be fancy
            text_loading.setVisibility(View.VISIBLE);
            PostsRepository.getInstance().getPostsFromUsers(localUser.getFollowing(), limit, before).addOnCompleteListener(findPostsTask -> {
                // this bit works exactly the same as ProfileFragment
                if (findPostsTask.isSuccessful()) {
                    List<Post> newPosts = findPostsTask.getResult();
                    if (newPosts.isEmpty()) {
                        if (posts.isEmpty()) {
                            text_noPosts.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (text_notFollowing.getVisibility() == View.VISIBLE) {
                            text_notFollowing.setVisibility(View.GONE);
                        }
                        if (text_noPosts.getVisibility() == View.VISIBLE) {
                            text_noPosts.setVisibility(View.GONE);
                        }
                        Log.d(TAG, "Loaded " + newPosts.size() + " new posts");
                        posts.addAll(newPosts);
                        // we want to add instances of PostFragment to the timeline. however, we
                        // also need to retrieve user information to complete the fragments.
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        // first, keep track of a list of the tasks that retrieve users
                        List<Task<User>> loadPostsTasks = new ArrayList<>();
                        // next, keep track of all fragments that are created in the process
                        Map<Post, PostFragment> frags = new HashMap<>(newPosts.size());
                        for (Post post : newPosts) {
                            // remember, getUserAsync also works with the user cache (usersPool),
                            // so these tasks should potentially be trivial, and thus, fast
                            Task<User> loadPostTask = getUserAsync(post.getUser()).addOnCompleteListener(task -> {
                               if (task.isSuccessful()) {
                                   User user = task.getResult();
                                   if (user != null) {
                                       PostFragment frag = PostFragment.newInstance(user, post);
                                       frag.setOnProfileClickListener(onProfileClickListener);
                                       frag.setIsPostLikedListener(isPostLikedListener);
                                       // don't actually add the fragment to the timeline yet
                                       frags.put(post, frag);
                                   } else {
                                       Log.d(TAG, String.format("Invalid user id (%s) from post (%s)", post.getUser(), post.getId()));
                                   }
                               } else {
                                   Log.e(TAG, "Could not load post", task.getException());
                               }
                            });
                            // make sure to add the task to the list
                            loadPostsTasks.add(loadPostTask);
                        }
                        Tasks.whenAllComplete(loadPostsTasks).addOnCompleteListener(task -> {
                            // make sure to do an inverse comparison for reverse, and thus
                            // descending, order
                            List<Post> sortedPosts = new ArrayList<>(frags.keySet());
                            Collections.sort(sortedPosts, (o1, o2) -> o2.getPosted().compareTo(o1.getPosted()));
                            for (Post post : sortedPosts) {
                                transaction.add(R.id.layout_timeline_posts, frags.get(post)).addToBackStack(null);
                            }
                            // finally commit the change
                            transaction.commit();
                            // nosql is fun...
                        });
                    }
                } else {
                    Log.w(TAG, "Could not load posts", findPostsTask.getException());
                }
                // remember to re-enable the button and "remove" the loading text
                text_loading.setVisibility(View.GONE);
                button_morePosts.setEnabled(true);
            });
        }
    }

}