package de.timonback.notipush.service;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.timonback.notipush.util.auth.SignInResultNotifier;

public class AuthenticationService {
    public interface AuthenticationSignedInListener {
        void onSignedIn();
    }

    private final FirebaseAuth mAuth;
    private final AuthenticationSignedInListener mListener;

    public AuthenticationService(AuthenticationSignedInListener listener) {
        mAuth = FirebaseAuth.getInstance();
        mListener = listener;
    }

    public void onStart(Activity activity) {
        // Default Database rules do not allow unauthenticated reads, so we need to
        // sign in before attaching the RecyclerView adapter otherwise the Adapter will
        // not be able to read any data from the Database.
        if (isSignedIn()) {
            mListener.onSignedIn();
        } else {
            signInAnonymously(activity);
        }
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public void signInAnonymously(Activity activity) {
        Toast.makeText(activity, "Signing in...", Toast.LENGTH_SHORT).show();
        mAuth.signInAnonymously()
                .addOnSuccessListener(activity, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult result) {
                        mListener.onSignedIn();
                    }
                })
                .addOnCompleteListener(new SignInResultNotifier(activity));
    }

    public boolean isSignedIn() {
        return mAuth.getCurrentUser() != null;
    }

    public void addAuthStateListener(FirebaseAuth.AuthStateListener listener) {
        mAuth.addAuthStateListener(listener);
    }

    public void removeAuthStateListener(FirebaseAuth.AuthStateListener listener) {
        mAuth.removeAuthStateListener(listener);
    }
}
