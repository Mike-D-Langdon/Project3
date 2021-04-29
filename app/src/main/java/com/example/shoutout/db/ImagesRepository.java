package com.example.shoutout.db;

import android.net.Uri;
import android.util.Log;

import com.example.shoutout.util.StringUtil;
import com.example.shoutout.util.UID;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;

import java.io.File;

/**
 * Repository for storing and retrieving images from a Firebase storage server. Images are
 * automatically renamed to a randomly-generated, and thus anonymous, file name before uploading.
 *
 * @author Corneilious Eanes
 * @since April 29, 2021
 */
public class ImagesRepository {

    /**
     * Logging tag
     */
    private static final String TAG = ImagesRepository.class.getSimpleName();

    /**
     * Reference to the Firebase storage server
     */
    private StorageReference ref;

    /**
     * Constructor that requires a Firebase storage reference
     * @param ref A Firebase storage reference
     */
    public ImagesRepository(StorageReference ref) {
        this.ref = ref;
    }

    /**
     * Uploads an image to the storage server from its file.
     * @param file The file pointing to the image to upload
     * @return A task that contains a string. If not null, this this points to the path of the
     * newly-uploaded image and can be retrieved by calling {@link #getUri(String)} or
     * {@link #getStream(String)}. If null, then the upload was not successful.
     */
    public Task<String> upload(File file) {
        final String randId = UID.generate();
        final String extension = StringUtil.getFileNameExtension(file.getName());
        final String path = randId + extension;
        return ref.child(path).putFile(Uri.fromFile(file)).continueWith(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Uploaded image: " + path);
                return path;
           }
           return null;
        });
    }

    /**
     * Uploads an image to the storage server from its file.
     * @param uri The URI pointing to the image to upload
     * @return A task that contains a string. If not null, this this points to the path of the
     * newly-uploaded image and can be retrieved by calling {@link #getUri(String)} or
     * {@link #getStream(String)}. If null, then the upload was not successful.
     */
    public Task<String> upload(Uri uri) {
        final String randId = UID.generate();
        final String extension = StringUtil.getFileNameExtension(uri.getLastPathSegment());
        final String path = randId + extension;
        return ref.child(path).putFile(uri).continueWith(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Uploaded image: " + path);
                return path;
            }
            return null;
        });
    }

    /**
     * Asynchronously retrieve a valid URI pointing to an image on the storage server.
     * @param path The path to the image. Initially should be retrieved from {@link #upload(Uri)}
     *             or {@link #upload(File)}
     * @return A task containing a {@link Uri}
     * @see StorageReference#getDownloadUrl()
     */
    public Task<Uri> getUri(String path) {
        return ref.child(path).getDownloadUrl();
    }

    /**
     * Asynchronously retrieve a valid stream pointing to an image on the storage server.
     * @param path The path to the image. Initially should be retrieved from {@link #upload(Uri)}
     *             or {@link #upload(File)}
     * @return A task containing the stream pointing to the image on the storage server
     * @see StorageReference#getStream()
     */
    public StreamDownloadTask getStream(String path) {
        return ref.child(path).getStream();
    }

    /**
     * Static instance of this repository
     */
    private static ImagesRepository instance = null;

    /**
     * Retrieve a static, guaranteed non-null instance of this repository.
     * @return A permanent instance of this repository
     */
    public static ImagesRepository getInstance() {
        if (instance == null) {
            // automaically
            instance = new ImagesRepository(FirebaseStorage.getInstance().getReference("images"));
        }
        return instance;
    }

}
