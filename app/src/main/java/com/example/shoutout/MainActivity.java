package com.example.shoutout;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shoutout.activity.fragment.PostFragment;
import com.example.shoutout.dbo.Post;

import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Random rand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rand = new Random();
    }

    public void debug_addPost(View view) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.layout_timeline_linear, PostFragment.newInstance("https://i.imgur.com/GZQKZ9N.png", "Hello!", "mycoolusername", "whaddup youtube gang its ya boi", "https://i.imgur.com/5oc6cPv.jpg", rand.nextInt(10), rand.nextInt(100), rand.nextInt(100)), UUID.randomUUID().toString())
                .addToBackStack(null)
                .commit();
    }

}