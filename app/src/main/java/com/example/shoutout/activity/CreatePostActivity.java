package com.example.shoutout.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoutout.R;
import com.example.shoutout.db.ImagesRepository;
import com.example.shoutout.db.PostsRepository;
import com.example.shoutout.util.StringUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.Collections;

public class CreatePostActivity extends AppCompatActivity {

    private static final String TAG = CreatePostActivity.class.getSimpleName();
    private static final int REQ_GALLERY = 0;

    private EditText et_bodyView;
    private Button btn_addImage;
    private TextView txt_imageFile;
    private ImageView img_preview;
    private Button btn_post;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        et_bodyView = findViewById(R.id.edit_createPost_body);
        btn_addImage = findViewById(R.id.button_createPost_addImage);
        txt_imageFile = findViewById(R.id.text_createPost_imageFile);
        img_preview = findViewById(R.id.image_createPost_preview);
        btn_post = findViewById(R.id.button_createPost_post);
        imageUri = null;

        btn_addImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQ_GALLERY);
        });

        btn_post.setOnClickListener(v -> {
            final String body = et_bodyView.getText().toString();
            if (imageUri != null) {
                ImagesRepository.getInstance().upload(imageUri).addOnCompleteListener(uploadImageTask -> {
                    if (uploadImageTask.isSuccessful()) {
                        final String path = uploadImageTask.getResult();
                        PostsRepository.getInstance().create(null, FirebaseAuth.getInstance().getCurrentUser().getUid(), body, Collections.singletonList(path)).addOnCompleteListener(createPostTask -> {
                           if (createPostTask.isSuccessful()) {
                               Intent intent = new Intent(this, MainActivity.class);
                               startActivity(intent);
                               finish();
                           } else {
                               Toast.makeText(this, "Could not create post!", Toast.LENGTH_SHORT).show();
                               Log.e(TAG, "Could not create post");
                           }
                        });
                    } else {
                        Toast.makeText(this, "Could not upload image!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Could not upload to webserver");
                    }
                });
            } else {
                PostsRepository.getInstance().create(null, FirebaseAuth.getInstance().getCurrentUser().getUid(), body, Collections.emptyList()).addOnCompleteListener(createPostTask -> {
                    if (createPostTask.isSuccessful()) {
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Could not create post!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Could not create post");
                    }
                });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_GALLERY) {
            if (resultCode == RESULT_OK) {
                imageUri = data.getData();
                txt_imageFile.setText(imageUri.getLastPathSegment());
                Picasso.get().load(imageUri).into(img_preview);
            } else {
                imageUri = null;
                txt_imageFile.setText("");
                img_preview.setImageDrawable(null);
            }
        }
    }

}