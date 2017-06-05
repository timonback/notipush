package de.timonback.notipush.service.authentication;

import android.app.Activity;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import de.timonback.notipush.util.auth.SignInResultNotifier;

public class AuthenticationService {
    private static AuthenticationService INSTANCE;
    private final FirebaseAuth mAuth;
    private final List<AuthenticationSignedInListener> listeners = new ArrayList<>();

    private AuthenticationService() {
        mAuth = FirebaseAuth.getInstance();
    }

    public static synchronized AuthenticationService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AuthenticationService();
        }
        return INSTANCE;
    }

    public void onStart(Activity activity) {
        // Default Database rules do not allow unauthenticated reads, so we need to
        // sign in before attaching the RecyclerView adapter otherwise the Adapter will
        // not be able to read any data from the Database.
        if (isSignedIn()) {
            fireOnSignedInListeners();
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
                        fireOnSignedInListeners();
                    }
                })
                .addOnCompleteListener(new SignInResultNotifier(activity));
    }

    public boolean isSignedIn() {
        return mAuth.getCurrentUser() != null;
    }

    private void fireOnSignedInListeners() {
        for (AuthenticationSignedInListener listener : listeners) {
            listener.onSignedIn();
        }
    }

    public void addAuthenticationSignedInListener(AuthenticationSignedInListener listener) {
        listeners.add(listener);
    }

    public void addAuthStateListener(FirebaseAuth.AuthStateListener listener) {
        mAuth.addAuthStateListener(listener);
    }

    public void removeAuthStateListener(FirebaseAuth.AuthStateListener listener) {
        mAuth.removeAuthStateListener(listener);
    }

    public interface AuthenticationSignedInListener {
        void onSignedIn();
    }
}
