package com.example.shoutout.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shoutout.R;
import com.example.shoutout.db.UsersRepository;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private  static final String TAG = SignUpActivity.class.getSimpleName();

    public static final String EXTRA_REGISTERED = "registered";

    private Button btn_signUp;
    private Button btn_cancel;
    private EditText et_username;
    private EditText et_email;
    private EditText et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        et_email = findViewById(R.id.edit_login_email);
        et_username = findViewById(R.id.edit_login_username);
        et_password = findViewById(R.id.edit_login_password);
        btn_signUp = findViewById(R.id.button_signup_register);
        btn_cancel = findViewById(R.id.button_signup_cancel);

        btn_signUp.setOnClickListener(v -> {
            Log.i(TAG, "onClick sign up button");

            final String username = et_username.getText().toString();
            final String email = et_email.getText().toString();
            final String password = et_password.getText().toString();

            UsersRepository.getInstance().isUsernameAvailable(username).addOnSuccessListener(usernameAvailable -> {
                if (usernameAvailable) {
                    Log.d(TAG, "Username available for: " + username);
                    UsersRepository.getInstance().isEmailAvailable(email).addOnSuccessListener(emailAvailable -> {
                        if (emailAvailable) {
                            Log.d(TAG, "Email available for: " + email);
                            Log.d(TAG, "Signing up...");
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
                                                    Log.w(TAG, "User could not be registered!", regTask.getException());
                                                }
                                            });
                                        } else {
                                            Log.w(TAG, "User not created!", createTask.getException());
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
