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

/**
 * Activity to allow the end user to create a new account. This allows the end user to specify the
 * following for their new account:
 * <ul>
 *     <li>Username</li>
 *     <li>Email address</li>
 *     <li>Password</li>
 * </ul>
 * The email address and password are only used to log in and are handled entirely by Firebase Auth.
 * The newly-created user's display name is set to be the same as the username. Important to note
 * is that this doesn't send an email confirmation like a production-level application would. So
 * you can effectively use any email you want.
 *
 * @author Corneilious Eanes, Margaret Hu
 * @since April 29, 2021
 */
public class SignUpActivity extends AppCompatActivity {

    /**
     * Logging tag
     */
    private  static final String TAG = SignUpActivity.class.getSimpleName();
    /**
     * Extra ID for whether the registration was successful
     */
    public static final String EXTRA_REGISTERED = "registered";

    /**
     * Button that allows the end user to officially sign up for a new account immediately
     */
    private Button btn_signUp;
    /**
     * Button that allows the end user to cancel out of creating a new account and directs them
     * back to the login activity
     */
    private Button btn_cancel;
    /**
     * Text field that allows the end user to set their starting username
     */
    private EditText et_username;
    /**
     * Text field that allows the end user to set their email address for authentication
     */
    private EditText et_email;
    /**
     * Text field that allows the end user to set their password for authentication
     */
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

            // first check if both the username and email address aren't being used by another
            // account. firebase database doesn't allow for OR-like request, so we have to use
            // two different requests, which is dumb unless im getting that completely wrong
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
                                            // set the user as being officially registered and
                                            // redirect back to the login activity
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

        // allow the end user to cancel out of creating a new account
        btn_cancel.setOnClickListener(v -> {
            Log.d(TAG, "onClick cancel button");
            Intent intent = new Intent();
            intent.putExtra(EXTRA_REGISTERED, false);
            setResult(RESULT_CANCELED, intent);
            finish();
        });
    }

}
