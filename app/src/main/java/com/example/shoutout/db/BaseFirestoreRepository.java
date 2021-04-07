package com.example.shoutout.db;

import com.google.firebase.firestore.CollectionReference;

public abstract class BaseFirestoreRepository {

    private CollectionReference col;

    public BaseFirestoreRepository(CollectionReference col) {
        this.col = col;
    }

    public CollectionReference getCollection() {
        return col;
    }

}
