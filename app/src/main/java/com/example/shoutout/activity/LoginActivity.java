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

/**
 * Activity that allows the end user to log into an account. For freshly-installed instances, this
 * will be the first screen you see. Optionally, you can also register for a new account if you
 * don't already have one.
 *
 * @author Corneilious Eanes, Margaret Hu
 * @since April 29, 2021
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Request ID for registering a new account
     */
    private static final int REQ_SIGNUP = 1;
    /**
     * Logging tag
     */
    public static String TAG = LoginActivity.class.getSimpleName();

    /**
     * Text field allowing the end user to specify their username
     */
    private EditText et_username;
    /**
     * Text field allowing the end user to specify their password
     */
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
        // if the user has come back from registering a new account, display a message for them
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
        // start the signup activity if the user wants to register for a new account
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivityForResult(intent, REQ_SIGNUP);
    }

    public void handleLogin(View view) {
        String email = et_username.getText().toString();
        String pwd = et_password.getText().toString();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        // let firebase auth do all the heavy lifting
        auth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "user signed in");
                Toast.makeText(this, "Successfully signed in", Toast.LENGTH_SHORT).show();
                auth.updateCurrentUser(task.getResult().getUser());
                startActivity(new Intent(this, MainActivity.class));
                // go back to the main activity
                finish();
            } else {
                Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "authentication failed");
            }
        });
    }

}
