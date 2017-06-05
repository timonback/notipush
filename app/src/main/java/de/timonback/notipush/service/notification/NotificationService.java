package de.timonback.notipush.service.notification;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

public class NotificationService {
    private final static String TAG = NotificationService.class.getName();

    private static NotificationService INSTANCE;

    private final List<String> topics = new LinkedList<>();
    private DatabaseReference mRef;

    private NotificationService() {
        mRef = FirebaseDatabase.getInstance().getReference();

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    topics.add(snapshot.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public static synchronized NotificationService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NotificationService();
        }
        return INSTANCE;
    }

    public Query getLastNotifications(String topic, int limit) {
        DatabaseReference mChatRef = mRef.child(topic);
        return mChatRef.limitToLast(limit);
    }

    public List<String> getTopics() {
        return topics;
    }
}
