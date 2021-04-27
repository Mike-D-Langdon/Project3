package com.example.shoutout.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shoutout.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private static final int REQ_SIGNUP = 1;

    public static String TAG = LoginActivity.class.getSimpleName();

    private EditText et_username;
    private EditText et_password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_username = findViewById(R.id.edit_login_username);
        et_password = findViewById(R.id.edit_login_password);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SIGNUP && resultCode == RESULT_OK) {
            boolean registered = data.getBooleanExtra(SignUpActivity.EXTRA_REGISTERED, false);
            if (registered) {
                Toast.makeText(this, "New user registered", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Could not register new user!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void handle_signUp(View view) {
        Log.d(TAG,"Sign up button clicked");
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivityForResult(intent, REQ_SIGNUP);
    }

    public void handleLogin(View view) {
        String email = et_username.getText().toString();
        String pwd = et_password.getText().toString();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "user signed in");
                Toast.makeText(this, "Successfully signed in", Toast.LENGTH_SHORT).show();
                auth.updateCurrentUser(task.getResult().getUser());
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "authentication failed");
            }
        });
    }

}
