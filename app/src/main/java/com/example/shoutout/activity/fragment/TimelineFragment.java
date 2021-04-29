package com.example.shoutout.activity.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.shoutout.R;
import com.example.shoutout.db.PostsRepository;
import com.example.shoutout.db.UsersRepository;
import com.example.shoutout.dbo.Post;
import com.example.shoutout.dbo.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class TimelineFragment extends Fragment {

    private static final String TAG = TimelineFragment.class.getSimpleName();

    public TimelineFragment() {
    }

    private AtomicReference<User> localUserRef;
    private Map<String, User> usersPool;
    private List<Post> posts;
    private UserSearchResultFragment.OnProfileClickListener onProfileClickListener;

    private LinearLayout layout_posts;
    private Button button_morePosts;
    private TextView text_notFollowing;
    private TextView text_noPosts;
    private TextView text_loading;
    private PostFragment.IsPostLikedListener isPostLikedListener;

    public static TimelineFragment newInstance() {
        TimelineFragment fragment = new TimelineFragment();
        return fragment;
    }

    public void setOnProfileClickListener(UserSearchResultFragment.OnProfileClickListener listener) {
        onProfileClickListener = listener;
    }

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
                Date before;
                if (posts.isEmpty()) {
                    before = new Date();
                } else {
                    before = posts.get(posts.size() - 1).getPosted();
                }
                loadMorePosts(30, before);
            });

            UsersRepository.getInstance().get(FirebaseAuth.getInstance().getUid()).addOnSuccessListener(user -> {
               localUserRef.set(user);
               button_morePosts.setEnabled(true);
               text_loading.setVisibility(View.GONE);

               if (user.getFollowing().isEmpty()) {
                   text_notFollowing.setVisibility(View.VISIBLE);
               } else {
                   loadMorePosts(20, new Date());
               }
            });
        }
    }

    private Task<User> getUserAsync(String id) {
        if (usersPool.containsKey(id)) {
            return Tasks.forResult(usersPool.get(id));
        }
        return UsersRepository.getInstance().get(id).addOnSuccessListener(user -> {
            if (user != null) {
                usersPool.put(user.getId(), user);
            }
        });
    }

    public void loadMorePosts(int limit, Date before) {
        final User localUser = localUserRef.get();
        if (localUser != null) {
            button_morePosts.setEnabled(false);
            text_loading.setVisibility(View.VISIBLE);
            PostsRepository.getInstance().getPostsFromUsers(localUser.getFollowing(), limit, before).addOnCompleteListener(findPostsTask -> {
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
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        List<Task<User>> loadPostsTasks = new ArrayList<>();
                        for (Post post : newPosts) {
                            Task<User> loadPostTask = getUserAsync(post.getUser()).addOnCompleteListener(task -> {
                               if (task.isSuccessful()) {
                                   User user = task.getResult();
                                   if (user != null) {
                                       PostFragment frag = PostFragment.newInstance(user, post);
                                       frag.setOnProfileClickListener(onProfileClickListener);
                                       frag.setIsPostLikedListener(isPostLikedListener);
                                       transaction.add(R.id.layout_timeline_posts, frag)
                                               .addToBackStack(null);
                                   } else {
                                       Log.d(TAG, String.format("Invalid user id (%s) from post (%s)", post.getUser(), post.getId()));
                                   }
                               } else {
                                   Log.e(TAG, "Could not load post", task.getException());
                               }
                            });
                            loadPostsTasks.add(loadPostTask);
                        }
                        posts.addAll(newPosts);
                        Tasks.whenAllComplete(loadPostsTasks).addOnCompleteListener(task -> {
                            transaction.commit();
                        });
                    }
                } else {
                    Log.w(TAG, "Could not load posts", findPostsTask.getException());
                }
                text_loading.setVisibility(View.GONE);
                button_morePosts.setEnabled(true);
            });
        }
    }

}