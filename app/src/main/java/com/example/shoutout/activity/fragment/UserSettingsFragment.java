package com.example.shoutout.activity.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.JsonWriter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.shoutout.R;
import com.example.shoutout.db.ImagesRepository;
import com.example.shoutout.db.UsersRepository;
import com.example.shoutout.dbo.User;
import com.example.shoutout.util.DateTimeUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import static android.app.Activity.RESULT_OK;

public class UserSettingsFragment extends Fragment {

    private static final String TAG = UserSettingsFragment.class.getSimpleName();
    private static final String ARG_USER = "user";
    private static final int REQUEST_SELECT_AVATAR = 0;

    public UserSettingsFragment() {
    }

    private User user;
    private Uri tempAvatarLocalUri;

    private ImageView img_avatar;
    private Button btn_changeAvatar;
    private EditText et_displayName;
    private EditText et_birthday;
    private EditText et_biography;
    private Button btn_saveChanges;

    public static UserSettingsFragment newInstance(User user) {
        UserSettingsFragment frag = new UserSettingsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        frag.setArguments(args);
        return frag;
    }

    private void checkEnableSaveButton() {
        boolean shouldEnable = false;
        if (user != null) {
            String newDisplayName = et_displayName.getText().toString();
            String newBirthdayStr = et_birthday.getText().toString();
            Date newBirthday;
            if (newBirthdayStr.isEmpty()) {
                newBirthday = null;
            } else {
                newBirthday = DateTimeUtil.parseSimpleDate(newBirthdayStr);
            }
            String newBiography = et_biography.getText().toString();
            // will only enable the button if the new display name is not empty, and any of the
            // fields' values are different those from the currently stored user
            if (!newDisplayName.isEmpty() &&
                    (user.getDisplayName().equals(newDisplayName) ||
                            user.getBiography().equals(newBiography) ||
                    (newBirthday != null && newBirthday.equals(user.getBirthday())))) {
                shouldEnable = true;
            }
        }
        if (tempAvatarLocalUri != null) {
            shouldEnable = true;
        }
        btn_saveChanges.setEnabled(shouldEnable);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().get(ARG_USER);
        }
        tempAvatarLocalUri = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getView() != null) {
            img_avatar = getView().findViewById(R.id.iv__edit_profile_image);
            btn_changeAvatar = getView().findViewById(R.id.btn_avatar);
            et_displayName = getView().findViewById(R.id.et_display_name);
            et_birthday = getView().findViewById(R.id.et_birthday);
            et_biography = getView().findViewById(R.id.et_biography);
            btn_saveChanges = getView().findViewById(R.id.btn_save);

            ImagesRepository.getInstance().getUri(user.getAvatarUri()).addOnSuccessListener(avatarUri -> {
                Picasso.get().load(avatarUri).fit().centerInside().into(img_avatar);
            });
            et_displayName.setText(user.getDisplayName());
            if (user.getBirthday() != null) {
                et_birthday.setText(DateTimeUtil.formatSimpleDate(user.getBirthday()));
            }
            et_biography.setText(user.getBiography());

            final TextWatcher textWatcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void afterTextChanged(Editable s) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    checkEnableSaveButton();
                }
            };

            et_displayName.addTextChangedListener(textWatcher);
            et_birthday.addTextChangedListener(textWatcher);
            et_biography.addTextChangedListener(textWatcher);

            btn_changeAvatar.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_SELECT_AVATAR);
            });

            btn_saveChanges.setOnClickListener(v -> {
                String newDisplayName = et_displayName.getText().toString();
                if (!user.getDisplayName().equals(newDisplayName)) {
                    String displayName = et_displayName.getText().toString();
                    if (displayName.isEmpty()) {
                        Toast.makeText(getContext(), "Display name cannot be empty", Toast.LENGTH_SHORT).show();
                    } else if (!displayName.equals(user.getDisplayName())) {
                        UsersRepository.getInstance()
                                .updateDisplayName(user.getId(), newDisplayName)
                                .addOnCompleteListener(updateTask -> {
                            if (updateTask.isSuccessful()) {
                                Log.i(TAG, "Display name updated");
                                user.setDisplayName(newDisplayName);
                                checkEnableSaveButton();
                            } else {
                                Log.w(TAG, "Could not update display name", updateTask.getException());
                            }
                        });
                    }
                }
                String newBirthdayStr = et_birthday.getText().toString();
                if (!newBirthdayStr.isEmpty()) {
                    Date newBirthday = DateTimeUtil.parseSimpleDate(newBirthdayStr);
                    if (newBirthday == null) {
                        Toast.makeText(getContext(), "Invalid date for birthday", Toast.LENGTH_SHORT).show();
                    } else {
                        UsersRepository.getInstance()
                                .updateBirthday(user.getId(), newBirthday)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Log.i(TAG, "Birthday updated");
                                        user.setBirthday(newBirthday);
                                        checkEnableSaveButton();
                                    } else {
                                        Log.w(TAG, "Could not update birthday", task.getException());
                                    }
                                });
                    }
                }
                String newBiography = et_biography.getText().toString();
                if (!newBiography.equals(user.getBiography())) {
                    UsersRepository.getInstance()
                            .updateBiography(user.getId(), newBiography)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.i(TAG, "Biography updated");
                                    user.setBiography(newBiography);
                                    checkEnableSaveButton();
                                } else {
                                    Log.w(TAG, "Could not update biography", task.getException());
                                }
                            });
                }
                if (tempAvatarLocalUri != null) {
                    ImagesRepository.getInstance().upload(tempAvatarLocalUri).addOnCompleteListener(uploadAvatarTask -> {
                        if (uploadAvatarTask.isSuccessful()) {
                            final String path = uploadAvatarTask.getResult();
                            UsersRepository.getInstance()
                                    .updateAvatar(user.getId(), path)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            Log.i(TAG, "Avatar successfully updated");
                                            user.setAvatarUri(path);
                                            checkEnableSaveButton();
                                        } else {
                                            Log.w(TAG, "Could not update avatar", updateTask.getException());
                                        }
                                    });
                        } else {
                            Log.w(TAG, "Could not upload avatar", uploadAvatarTask.getException());
                        }
                    });

                }
            });
            btn_saveChanges.setEnabled(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_AVATAR) {
            if (resultCode == RESULT_OK) {
                tempAvatarLocalUri = data.getData();
                Picasso.get().load(tempAvatarLocalUri).into(img_avatar);
                btn_saveChanges.setEnabled(true);
            } else {
                tempAvatarLocalUri = null;
                ImagesRepository.getInstance().getUri(user.getAvatarUri()).addOnSuccessListener(uri -> {
                    Picasso.get().load(uri).into(img_avatar);
                });
                checkEnableSaveButton();
            }
        }
    }

}
