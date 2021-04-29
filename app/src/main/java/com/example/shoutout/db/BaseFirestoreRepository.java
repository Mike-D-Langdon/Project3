package com.example.shoutout.db;

import com.google.firebase.firestore.CollectionReference;

/**
 * Simple, abstract class used as a template for Firestore-based repository classes.
 *
 * @author Corneilious Eanes
 * @since April 29, 2021
 */
public abstract class BaseFirestoreRepository {

    /**
     * Reference to the Firestore-based collection
     */
    private CollectionReference col;

    /**
     * Constructor that requires a collection reference
     * @param col Reference to the Firestore-based collection
     */
    public BaseFirestoreRepository(CollectionReference col) {
        this.col = col;
    }

    /**
     * Get the reference to the Firestore collection. This is very powerful, so be careful when
     * using it.
     * @return Reference to the Firestore collection
     */
    public CollectionReference getCollection() {
        return col;
    }

}
