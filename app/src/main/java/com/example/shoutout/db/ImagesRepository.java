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

public class ImagesRepository {

    private static final String TAG = ImagesRepository.class.getSimpleName();

    private StorageReference ref;

    public ImagesRepository(StorageReference ref) {
        this.ref = ref;
    }

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

    public Task<Uri> getUri(String fullPath) {
        return ref.child(fullPath).getDownloadUrl();
    }

    public StreamDownloadTask getStream(String fullPath) {
        return ref.child(fullPath).getStream();
    }

    private static ImagesRepository instance = null;

    public static ImagesRepository getInstance() {
        if (instance == null) {
            instance = new ImagesRepository(FirebaseStorage.getInstance().getReference("images"));
        }
        return instance;
    }

}
