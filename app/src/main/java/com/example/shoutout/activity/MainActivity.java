package com.example.shoutout.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shoutout.R;
import com.example.shoutout.activity.fragment.ProfileFragment;
import com.example.shoutout.activity.fragment.SearchFragment;
import com.example.shoutout.activity.fragment.TimelineFragment;
import com.example.shoutout.activity.fragment.UserSettingsFragment;
import com.example.shoutout.db.UsersRepository;
import com.example.shoutout.dbo.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Activity that is the core of the entire application. Allows the end user to navigate between
 * several fragments and screens, including
 * <ul>
 *     <li>Viewing their timeline</li>
 *     <li>Searching for users</li>
 *     <li>Composing a new post</li>
 *     <li>Viewing their profile</li>
 *     <li>Modifying their settings</li>
 * </ul>
 * Also features a logout button that will sign the user out and redirect them to the signin
 * activity.
 *
 * @author Corneilious Eanes, Margaret Hu
 * @since April 29, 2021
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Logging tag
     */
    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Reference for a user to be featured in a {@link ProfileFragment}. If the internal reference
     * is null, then the fragment will feature the logged-in local user. This is used when an
     * internal fragment calls
     * {@link com.example.shoutout.activity.fragment.UserSearchResultFragment.OnProfileClickListener},
     * and the internal reference is set back to null once used.
     */
    private AtomicReference<User> viewUserRef;
    /**
     * Bottom navigation menu. This allows the end user to perform all major actions in the app.
     * @see MainActivity
     */
    private BottomNavigationView nav;
    /**
     * The layout where all action-based fragments are loaded into
     */
    private LinearLayout layout_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewUserRef = new AtomicReference<>();
        nav = findViewById(R.id.bottomNavigation);
        layout_main = findViewById(R.id.layout_main);

        // redirect the end user to the login activity if they're not logged in
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        nav.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.action_compose) {
                // the only action to warrant a new activity. better idea would be to incorporate
                // this as its own fragment
                Intent intent = new Intent(this, CreatePostActivity.class);
                startActivity(intent);
                finish();
                return true;
                // for all other actions, they use some sort of fragment that is embedded into
                // layout_main, so all views inside of layout_main must be removed first
            } else if (item.getItemId() == R.id.action_profile) {
                layout_main.removeAllViews();
                // the firebase auth user's UID is the same as the ID used in User and UsersRepository
                UsersRepository.getInstance().get(auth.getUid()).addOnSuccessListener(loggedInUser -> {
                    // determine which profile to view. if the internal reference is null, then
                    // view the profile of the logged-in local user
                    // in effect, if the end user simply clicks on the profile icon, then the
                    // internal reference will be null, and the activity will direct them to their
                    // own profile
                    User viewUser = viewUserRef.get();
                    if (viewUser == null) {
                        viewUser = loggedInUser;
                    }
                    ProfileFragment frag = ProfileFragment.newInstance(viewUser, loggedInUser);
                    // remember to set the post liked listener
                    frag.setIsPostLikedListener(postId -> UsersRepository.getInstance().get(auth.getUid()).continueWith(getUserTask -> {
                        if (getUserTask.isSuccessful()) {
                            User user = getUserTask.getResult();
                            if (user != null) {
                                return user.getLikes().contains(postId);
                            }
                        }
                        return false;
                    }));
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.layout_main, frag)
                            .addToBackStack(null)
                            .commit();
                    // reset the internal reference
                    viewUserRef.set(null);
                });
                return true;
            } else if (item.getItemId() == R.id.action_settings) {
                layout_main.removeAllViews();
                UsersRepository.getInstance().get(auth.getUid()).addOnSuccessListener(user -> {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.layout_main, UserSettingsFragment.newInstance(user))
                            .addToBackStack(null)
                            .commit();
                });
                return true;
            } else if (item.getItemId() == R.id.action_search) {
                layout_main.removeAllViews();
                SearchFragment frag = SearchFragment.newInstance();
                // don't forget about the profile click listener. this allows the end user to view
                // the profiles of non-followed users and potentially follow them
                frag.setOnProfileClickListener(user -> {
                    viewUserRef.set(user);
                    nav.setSelectedItemId(R.id.action_profile);
                });
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.layout_main, frag)
                        .addToBackStack(null)
                        .commit();
                return true;
            } else if (item.getItemId() == R.id.action_home) {
                layout_main.removeAllViews();
                UsersRepository.getInstance().get(auth.getUid()).addOnSuccessListener(loggedInUser -> {
                    TimelineFragment frag = TimelineFragment.newInstance();
                    // need both like and profile click listeners for this one
                    frag.setOnProfileClickListener(user -> {
                        viewUserRef.set(user);
                        nav.setSelectedItemId(R.id.action_profile);
                    });
                    frag.setIsPostLikedListener(postId -> UsersRepository.getInstance().get(auth.getUid()).continueWith(getUserTask -> {
                       if (getUserTask.isSuccessful()) {
                           User user = getUserTask.getResult();
                           if (user != null) {
                               return user.getLikes().contains(postId);
                           }
                       }
                       return false;
                    }));
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.layout_main, frag)
                            .addToBackStack(null)
                            .commit();
                });
                return true;
            }
            // if none of the above actions were triggered, then don't update the selection
            return false;
        });
        nav.setSelectedItemId(R.id.action_home);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // log out the user if they click the appropriate button
        if (item.getItemId() == R.id.action_logout) {
            Toast.makeText(this, "Successfully logged out", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}