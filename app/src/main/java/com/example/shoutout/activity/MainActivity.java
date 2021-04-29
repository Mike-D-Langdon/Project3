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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Random rand;

    // used for viewing a user's profile. null = visit the logged in user's profile
    private AtomicReference<User> viewUserRef;
    private BottomNavigationView nav;
    private LinearLayout layout_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewUserRef = new AtomicReference<>();
        nav = findViewById(R.id.bottomNavigation);
        layout_main = findViewById(R.id.layout_main);

        rand = new Random();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        nav.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.action_compose) {
                Intent intent = new Intent(this, CreatePostActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (item.getItemId() == R.id.action_profile) {
                layout_main.removeAllViews();
                UsersRepository.getInstance().get(auth.getUid()).addOnSuccessListener(loggedInUser -> {
                    User viewUser = viewUserRef.get();
                    if (viewUser == null) {
                        viewUser = loggedInUser;
                    }
                    ProfileFragment frag = ProfileFragment.newInstance(viewUser, loggedInUser);
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
                    frag.setOnProfileClickListener(user -> {
                        viewUserRef.set(user);
                        nav.setSelectedItemId(R.id.action_profile);
                    });
                    frag.setIsPostLikedListener(postId -> {
                        return UsersRepository.getInstance().get(auth.getUid()).continueWith(getUserTask -> {
                           if (getUserTask.isSuccessful()) {
                               User user = getUserTask.getResult();
                               if (user != null) {
                                   return user.getLikes().contains(postId);
                               }
                           }
                           return false;
                        });
                    });
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.layout_main, frag)
                            .addToBackStack(null)
                            .commit();
                });
                return true;
            }
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