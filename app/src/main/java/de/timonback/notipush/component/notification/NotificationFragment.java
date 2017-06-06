package de.timonback.notipush.component.notification;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.Query;

import de.timonback.notipush.R;
import de.timonback.notipush.service.authentication.AuthenticationService;
import de.timonback.notipush.service.notification.NotificationService;
import de.timonback.notipush.service.notification.NotificationSettings;
import de.timonback.notipush.util.listview.FirebaseRecyclerAdapter;

public class NotificationFragment extends Fragment {
    public static final String INTENT_CHAT = "intent_chat";
    private static final String TAG = Notification.class.getName();
    private RecyclerView mMessages;
    private LinearLayoutManager mManager;
    private FirebaseRecyclerAdapter<Notification, NotificationHolder> mAdapter;
    private TextView mEmptyListMessage;

    private final AuthenticationService.AuthenticationSignedInListener authenticationSignedInListener
            = new AuthenticationService.AuthenticationSignedInListener() {
        @Override
        public void onSignedIn() {
            loadNotifications(getCurrentTopic());
        }
    };
    private final NotificationSettings.ChangeListener notificationChangeListener
            = new NotificationSettings.ChangeListener() {
        @Override
        public void topicChanged(String newTopic) {
            loadNotifications(newTopic);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AuthenticationService.getInstance().addAuthenticationSignedInListener(authenticationSignedInListener);
        NotificationSettings.getInstance(getActivity()).addChangeListener(notificationChangeListener);

        mEmptyListMessage = (TextView) getActivity().findViewById(R.id.emptyTextView);

        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);

        mMessages = (RecyclerView) getActivity().findViewById(R.id.messagesList);
        mMessages.setHasFixedSize(false);
        mMessages.setLayoutManager(mManager);

        final SwipeRefreshLayout mRefresh = (SwipeRefreshLayout) getActivity().findViewById(R.id.swiperefresh);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadNotifications(getCurrentTopic());

                mRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            String intent_topic = extras.getString(INTENT_CHAT);
            if (intent_topic != null) {
                Log.i(TAG, "got called with intent for chat " + intent_topic);
                NotificationSettings.getInstance(getActivity()).setCurrentTopic(intent_topic);
            }
        }

        AuthenticationService.getInstance().onStart(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();

        NotificationSettings.getInstance(getActivity()).removeChangeListener(notificationChangeListener);
        AuthenticationService.getInstance().removeAuthenticationSignedInListener(authenticationSignedInListener);

        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    private void loadNotifications(String topic) {
        Query query = NotificationService.getInstance().getLastNotifications(topic, 50);

        if (mAdapter != null) {
            mAdapter.cleanup();
        }

        mAdapter = new FirebaseRecyclerAdapter<Notification, NotificationHolder>(
                Notification.class, R.layout.message, NotificationHolder.class, query) {
            @Override
            public void populateViewHolder(NotificationHolder holder, Notification chat, int position) {
                holder.setMessage(chat.getMessage());
                holder.setDate(chat.getDate());
                holder.setIsSender(false);
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

    private String getCurrentTopic() {
        return NotificationSettings.getInstance(getActivity().getApplicationContext()).getCurrentTopic();
    }
}
