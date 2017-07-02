package de.timonback.notipush.service;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.LinkedList;
import java.util.List;

import de.timonback.notipush.service.authentication.AuthenticationService;

public class SubscriptionService {
    private static final String TAG = SubscriptionService.class.getName();

    private static SubscriptionService instance;

    private final FirebaseUser mUser;
    private final DatabaseReference mRef;

    private final List<String> subscriptions = new LinkedList<>();

    private SubscriptionService(FirebaseUser user) {
        mUser = user;
        mRef = FirebaseDatabase.getInstance().getReference().
                child("users").
                child(mUser.getUid()).
                child("subscriptions");

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                subscriptions.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String topic = snapshot.getKey();
                    subscriptions.add(topic);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public static synchronized SubscriptionService getInstance() {
        if (instance == null) {
            FirebaseUser user = AuthenticationService.getInstance().getCurrentUser();
            instance = new SubscriptionService(user);
        }
        return instance;
    }

    public void addChangeListener(final ChangeListener listener) {
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.update();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.update();
            }
        });
    }

    public List<String> getSubscribedTopics() {
        return subscriptions;
    }

    public void subscribeToTopic(String topic) {
        if(!subscriptions.contains(topic)) {
            FirebaseMessaging.getInstance().subscribeToTopic(topic);

            mRef.child(topic).setValue(true);
        }
    }

    public void unsubscribeFromTopic(String topic) {
        if(subscriptions.contains(topic)) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);

            mRef.child(topic).removeValue();
        }
    }
}