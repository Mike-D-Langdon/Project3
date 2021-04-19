package com.example.shoutout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    public static String TAG = "LoginActivity";
    EditText et_username;
    EditText et_password;
    Button btn_login;
    Button btn_sign_up;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.login);
        btn_sign_up = findViewById(R.id.sign_up);
    }

    public void handle_signUp(View view) {
        Log.d(TAG,"Sign up button clicked");
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }
}
