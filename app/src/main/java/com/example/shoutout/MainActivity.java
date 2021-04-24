package com.example.shoutout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shoutout.activity.fragment.PostFragment;
import com.example.shoutout.db.UsersRepository;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Random rand;
    private FirebaseAuth auth;
    private GoogleSignInClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rand = new Random();

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(this, gso);*/
    }

    public void debug_addPost(View view) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.layout_timeline_linear, PostFragment.newInstance("https://i.imgur.com/GZQKZ9N.png", "Hello!", "mycoolusername", "whaddup youtube gang its ya boi", "https://i.imgur.com/5oc6cPv.jpg", rand.nextInt(10), rand.nextInt(100), rand.nextInt(100)), UUID.randomUUID().toString())
                .addToBackStack(null)
                .commit();
    }

}