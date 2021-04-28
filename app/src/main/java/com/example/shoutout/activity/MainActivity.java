package com.example.shoutout.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shoutout.R;
import com.example.shoutout.activity.fragment.PostFragment;
import com.example.shoutout.db.ImagesRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Random rand;

    private BottomNavigationView nav;
    private Button btn_debug_createPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nav = findViewById(R.id.bottomNavigation);

        rand = new Random();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
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
            }
            return false;
        });

        btn_debug_createPost = findViewById(R.id.button_main_debugCreatePost);
        btn_debug_createPost.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreatePostActivity.class);
            startActivity(intent);
            finish();
        });
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

    public void debug_addPost(View view) {
        ImagesRepository.getInstance().getUri("1920x1080test.jpg").addOnSuccessListener(testBg -> {
            ImagesRepository.getInstance().getUri("avatartest.png").addOnSuccessListener(avatar -> {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.layout_timeline_linear, PostFragment.newInstance(avatar.getEncodedPath(), "My Display Name", "mycoolusername", "whaddup youtube gang its ya boi", testBg.getEncodedPath(), rand.nextInt(10), rand.nextInt(100), rand.nextInt(100)), UUID.randomUUID().toString())
                        .addToBackStack(null)
                        .commit();
            });
        });
    }

}