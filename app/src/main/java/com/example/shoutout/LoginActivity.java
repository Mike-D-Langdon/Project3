package com.example.shoutout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private static final int REQ_SIGNUP = 1;

    public static String TAG = LoginActivity.class.getSimpleName();
    EditText et_username;
    EditText et_password;
    Button btn_login;
    Button btn_sign_up;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_username = findViewById(R.id.edit_login_username);
        et_password = findViewById(R.id.edit_login_password);
        btn_login = findViewById(R.id.button_login);
        btn_sign_up = findViewById(R.id.button_signup);
    }

    public void handle_signUp(View view) {
        Log.d(TAG,"Sign up button clicked");
        Intent intent = new Intent(this, SignUp.class);
        startActivityForResult(intent, REQ_SIGNUP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SIGNUP && resultCode == RESULT_OK) {
            boolean registered = data.getBooleanExtra(SignUp.EXTRA_REGISTERED, false);
            if (registered) {
                Toast.makeText(this, "New user registered", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Could not register new user!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void handleLogin(View view) {
        String email = et_username.getText().toString();
        String pwd = et_password.getText().toString();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "user signed in");
                auth.updateCurrentUser(task.getResult().getUser());
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Log.d(TAG, "authentication failed");
            }
        });
    }

}
