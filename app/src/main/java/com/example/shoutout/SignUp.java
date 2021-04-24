package com.example.shoutout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shoutout.db.UsersRepository;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {

    public static final String EXTRA_REGISTERED = "registered";

    public static String TAG = "SignUp";
    EditText et_email;
    EditText et_username;
    EditText et_password;
    Button btn_sign_up;
    Button btn_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        et_email = findViewById(R.id.edit_login_email);
        et_username = findViewById(R.id.edit_login_username);
        et_password = findViewById(R.id.edit_login_password);
        btn_sign_up = findViewById(R.id.button_signup);
        btn_cancel = findViewById(R.id.cancel);

        btn_sign_up.setOnClickListener(v -> {
            Log.i(TAG, "onClick sign up button");

            final String username = et_username.getText().toString();
            final String email = et_email.getText().toString();
            final String password = et_password.getText().toString();

            UsersRepository.getInstance().isUsernameAvailable(username).addOnSuccessListener(usernameAvailable -> {
                if (usernameAvailable) {
                    UsersRepository.getInstance().isEmailAvailable(email).addOnSuccessListener(emailAvailable -> {
                        if (emailAvailable) {
                            FirebaseAuth.getInstance()
                                    .createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(createTask -> {
                                        Intent intent = new Intent();
                                        if (createTask.isSuccessful()) {
                                            Log.d(TAG, "User officially signed up");
                                            intent.putExtra(EXTRA_REGISTERED, true);
                                            AuthResult result = createTask.getResult();
                                            UsersRepository.getInstance()
                                                    .registerNewUser(result.getUser().getUid(), username, email)
                                                    .addOnCompleteListener(regTask -> {
                                                if (regTask.isSuccessful()) {
                                                    Log.d(TAG, "User officially registered: " + result.getUser().getUid() + ", " + username + ", " + email);
                                                } else {
                                                    Log.w(TAG, "User could not be registered!");
                                                }
                                            });
                                        } else {
                                            Log.w(TAG, "User not created!");
                                            intent.putExtra(EXTRA_REGISTERED, false);
                                        }
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    });
                        } else {
                            Toast.makeText(this, "Email address is already taken!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "Username is already taken!", Toast.LENGTH_SHORT).show();
                }
            });
        });
        btn_cancel.setOnClickListener(v -> {
            Log.d(TAG, "onClick cancel button");
            Intent intent = new Intent();
            intent.putExtra(EXTRA_REGISTERED, false);
            setResult(RESULT_CANCELED, intent);
            finish();
        });
    }

}
