package de.timonback.notipush.message;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import de.timonback.notipush.R;
import de.timonback.notipush.components.listview.FirebaseRecyclerAdapter;
import de.timonback.notipush.service.AuthenticationService;
import de.timonback.notipush.util.auth.SignInResultNotifier;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class NotificationActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {
    private static final String TAG = "RecyclerViewDemo";

    private AuthenticationService mAuthService;
    private DatabaseReference mRef;
    private DatabaseReference mChatRef;
    private Button mSendButton;
    private EditText mMessageEdit;

    private RecyclerView mMessages;
    private LinearLayoutManager mManager;
    private FirebaseRecyclerAdapter<Notification, NotificationHolder> mAdapter;
    private TextView mEmptyListMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        mAuthService = new AuthenticationService(new AuthenticationService.AuthenticationSignedInListener() {
            @Override
            public void onSignedIn() {
                attachRecyclerViewAdapter();
            }
        });

        mSendButton = (Button) findViewById(R.id.sendButton);
        mMessageEdit = (EditText) findViewById(R.id.messageEdit);
        mEmptyListMessage = (TextView) findViewById(R.id.emptyTextView);

        mRef = FirebaseDatabase.getInstance().getReference();
        mChatRef = mRef.child("chats");

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = mAuthService.getCurrentUser().getUid();
                String name = "User " + uid.substring(0, 6);

                Notification chat = new Notification(name, mMessageEdit.getText().toString(), uid);
                mChatRef.push().setValue(chat, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError error, DatabaseReference reference) {
                        if (error != null) {
                            Log.e(TAG, "Failed to write message", error.toException());
                        }
                    }
                });

                mMessageEdit.setText("");
            }
        });

        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(false);

        mMessages = (RecyclerView) findViewById(R.id.messagesList);
        mMessages.setHasFixedSize(false);
        mMessages.setLayoutManager(mManager);
    }

    @Override
    public void onStart() {
        super.onStart();

        mAuthService.onStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAuthService != null) {
            mAuthService.removeAuthStateListener(this);
        }
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        updateUI();
    }

    private void attachRecyclerViewAdapter() {
        Query lastFifty = mChatRef.limitToLast(50);
        mAdapter = new FirebaseRecyclerAdapter<Notification, NotificationHolder>(
                Notification.class, R.layout.message, NotificationHolder.class, lastFifty) {
            @Override
            public void populateViewHolder(NotificationHolder holder, Notification chat, int position) {
                holder.setName(chat.getName());
                holder.setText(chat.getMessage());

                FirebaseUser currentUser = mAuthService.getCurrentUser();
                if (currentUser != null && chat.getUid().equals(currentUser.getUid())) {
                    holder.setIsSender(true);
                } else {
                    holder.setIsSender(false);
                }
            }

            @Override
            protected void onDataChanged() {
                // If there are no chat messages, show a view that invites the user to add a message.
                mEmptyListMessage.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        };

        // Scroll to bottom on new messages
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mManager.smoothScrollToPosition(mMessages, null, mAdapter.getItemCount());
            }
        });

        mMessages.setAdapter(mAdapter);
    }

    private void updateUI() {
        // Sending only allowed when signed in
        mSendButton.setEnabled(mAuthService.isSignedIn());
        mMessageEdit.setEnabled(mAuthService.isSignedIn());
    }
}
