package com.example.shoutout;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.shoutout.dbo.User;

public class SignUp extends AppCompatActivity{

    public static String TAG = "SignUp";
    EditText et_first_name;
    EditText et_last_name;
    EditText et_email;
    EditText et_username;
    EditText et_password;
    Button btn_sign_up;
    Button btn_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //link layout components
        et_first_name = findViewById(R.id.et_first_name);
        et_last_name = findViewById(R.id.et_last_name);
        et_email = findViewById(R.id.et_email);
        et_username = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        btn_sign_up = findViewById(R.id.sign_up);
        btn_cancel = findViewById(R.id.cancel);

        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick sign up button");
                User user = new User();
                user.setUsername(et_username.getText().toString());
                user.setPwd(et_password.getText().toString());
            }
        });
    }
}
