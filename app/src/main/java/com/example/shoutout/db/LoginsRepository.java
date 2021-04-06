package com.example.shoutout.db;

import android.util.Log;

import com.example.shoutout.dbo.Login;
import com.example.shoutout.util.DateTimeUtil;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class LoginsRepository {

    private static final String TAG = "LoginRepo";

    private CollectionReference col;

    public LoginsRepository(CollectionReference col) {
        this.col = col;
    }

    public Optional<Login> get(UUID token) {
        DocumentSnapshot result = col.document(token.toString()).get().getResult();
        if (result.exists()) {
            return Optional.of(convert(result.toObject(LoginDBO.class)));
        }
        return Optional.empty();
    }

    public Optional<Login> get(UUID userId, String userAgent) {
        return col.whereEqualTo("userId", userId.toString())
                .whereEqualTo("userAgent", userAgent)
                .get().getResult().getDocuments()
                .stream().findAny()
                .map(doc -> convert(doc.toObject(LoginDBO.class)));
    }

    public boolean checkLoggedIn(UUID token) {
        return get(token).map(login -> {
            if (LocalDateTime.now().isAfter(login.getExpires())) {
                delete(token);
                return false;
            }
            return true;
        }).orElse(false);
    }

    public boolean loginAvailable(UUID userId, String userAgent) {
        return col.whereEqualTo("userId", userId.toString())
                .whereEqualTo("userAgent", userAgent)
                .get().getResult().getDocuments().isEmpty();
    }

    public UUID create(UUID userId, String userAgent) {
        // delete a pre-existing login if this conflicts
        get(userId, userAgent).ifPresent(prevLogin -> delete(prevLogin.getToken()));

        UUID token = UUID.randomUUID();
        Login login = new Login(
                token,
                userId,
                userAgent,
                LocalDateTime.now(),
                // will automatically log out in 30 days
                LocalDateTime.now().plusDays(30)
        );
        LoginDBO dbo = new LoginDBO(login);
        col.document(dbo.token).set(dbo)
                .addOnSuccessListener(task -> Log.i(TAG, String.format("Login credentials successfully created: %s (%s)", userId, userAgent)))
                .addOnFailureListener(e -> Log.w(TAG, String.format("Could not create login credentials for %s (%s)", userId, userAgent), e));
        return token;
    }

    public void delete(UUID token) {
        col.document(token.toString()).delete()
                .addOnSuccessListener(task -> Log.i(TAG, String.format("Deleted login credentials for %s", token)))
                .addOnFailureListener(e -> Log.w(TAG, "Could not delete login credentials", e));
    }

    private static class LoginDBO {
        public String token;
        public String userId;
        public String userAgent;
        public long created;
        public long expires;
        public LoginDBO(Login login) {
            token = login.getToken().toString();
            userId = login.getUserId().toString();
            userAgent = login.getUserAgent();
            created = DateTimeUtil.formatDateTime(login.getCreated());
            expires = DateTimeUtil.formatDateTime(login.getExpires());
        }
        public LoginDBO() {
        }
    }

    private static Login convert(LoginDBO login) {
        return new Login(
                UUID.fromString(login.token),
                UUID.fromString(login.userId),
                login.userAgent,
                DateTimeUtil.parseDateTime(login.created),
                DateTimeUtil.parseDateTime(login.expires)
        );
    }

}
