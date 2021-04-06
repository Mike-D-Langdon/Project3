package com.example.shoutout.util;

import com.google.firebase.firestore.FirebaseFirestore;

public class DatabaseUtil {

    private static FirebaseFirestore store = null;

    public static FirebaseFirestore get() {
        if (store == null) {
            store = FirebaseFirestore.getInstance();
        }
        return store;
    }

}
