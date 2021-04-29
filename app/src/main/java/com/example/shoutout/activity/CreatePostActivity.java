package com.example.shoutout.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shoutout.R;
import com.example.shoutout.db.ImagesRepository;
import com.example.shoutout.db.PostsRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.Collections;

/**
 * Activity allowing the end user to create a new post. The end user can specify the following:
 * <ul>
 *     <li>Text body</li>
 *     <li>A single image</li>
 * </ul>
 * Both of which are optional (probably not a good idea). All other fields are automatically filled
 * in by the application.
 *
 * @author Corneilious Eanes
 * @since April 29, 2021
 */
public class CreatePostActivity extends AppCompatActivity {

    /**
     * Logging tag
     */
    private static final String TAG = CreatePostActivity.class.getSimpleName();
    /**
     * Request ID for selecting an image to attach to the post
     */
    private static final int REQ_GALLERY = 0;

    /**
     * Text field that allows the end user to set the post's text body (may be empty)
     */
    private EditText et_bodyView;
    /**
     * Button that allows the end user to upload an image to go along with their post (optional)
     */
    private Button btn_addImage;
    /**
     * Text view that shows the file name of the image (not used; can't get that information from
     * URIs)
     */
    private TextView txt_imageFile;
    /**
     * Image view that shows the image that the end user wants to upload with their post
     */
    private ImageView img_preview;
    /**
     * Button that allows the end user to publish their post online
     */
    private Button btn_post;
    /**
     * Button that allows the end user to cancel their post, taking them back to the main activity
     */
    private Button btn_cancel;
    /**
     * URI that corresponds to the local image that the user wants to upload to the storage server
     */
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
        btn_cancel = findViewById(R.id.button_createPost_cancel);
        imageUri = null;

        // start an android-provided activity to allow the user to select an image
        btn_addImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQ_GALLERY);
        });

        btn_post.setOnClickListener(v -> {
            final String body = et_bodyView.getText().toString();
            // either create a post with a singleton list of images or an empty one. this project
            // was originally designed to support multiple images in one post, so that's why that's
            // a thing still.
            if (imageUri != null) {
                ImagesRepository.getInstance().upload(imageUri).addOnCompleteListener(uploadImageTask -> {
                    // create the post after the image has been uploaded to the storage server
                    if (uploadImageTask.isSuccessful()) {
                        final String path = uploadImageTask.getResult();
                        PostsRepository.getInstance().create(null, FirebaseAuth.getInstance().getCurrentUser().getUid(), body, Collections.singletonList(path)).addOnCompleteListener(createPostTask -> {
                           if (createPostTask.isSuccessful()) {
                               // go back to the main activity when the post upload is complete
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
                // we don't need to upload any images, so we can immediately create a new post
                PostsRepository.getInstance().create(null, FirebaseAuth.getInstance().getCurrentUser().getUid(), body, Collections.emptyList()).addOnCompleteListener(createPostTask -> {
                    if (createPostTask.isSuccessful()) {
                        // see comments above
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

        // allow the end user to go back to the main activity without posting anything
        btn_cancel.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_GALLERY) {
            if (resultCode == RESULT_OK) {
                imageUri = data.getData();
                // can't get the original file name from the Uri
                //txt_imageFile.setText(imageUri.getLastPathSegment());
                Picasso.get().load(imageUri).fit().centerInside().into(img_preview);
                img_preview.setVisibility(View.VISIBLE);
            } else {
                // if the activity was cancelled, then interpret that as the end user not wanting
                // to upload an image at all
                imageUri = null;
                //txt_imageFile.setText("");
                img_preview.setImageDrawable(null);
                img_preview.setVisibility(View.INVISIBLE);
            }
        }
    }

}