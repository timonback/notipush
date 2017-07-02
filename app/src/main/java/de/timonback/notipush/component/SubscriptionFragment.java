package de.timonback.notipush.component;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

import de.timonback.notipush.R;
import de.timonback.notipush.service.ChangeListener;
import de.timonback.notipush.service.SubscriptionService;
import de.timonback.notipush.service.notification.NotificationService;

public class SubscriptionFragment extends Fragment {
    private static final String TAG = SubscriptionFragment.class.getSimpleName();

    class DataSet {
        public DataSet(String topic, boolean subscribed) {
            this.topic = topic;
            this.subscribed = subscribed;
        }

        private String topic;
        private boolean subscribed;

        public String getTopic() {
            return topic;
        }

        public boolean isSubscribed() {
            return subscribed;
        }
    }
    private class SubscriptionViewHolder extends RecyclerView.ViewHolder {
        private CheckBox subscriptionCheckbox;

        public SubscriptionViewHolder(View itemView) {
            super(itemView);

            subscriptionCheckbox = (CheckBox) itemView.findViewById(R.id.subscription_checkbox);

            subscriptionCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String topic = buttonView.getText().toString();

                    if(isChecked) {
                        SubscriptionService.getInstance().subscribeToTopic(topic);
                    } else {
                        SubscriptionService.getInstance().unsubscribeFromTopic(topic);
                    }
                }
            });
        }
    }

    private ArrayList<DataSet> subscriptions = new ArrayList();

    private final RecyclerView.Adapter mAdapter = new RecyclerView.Adapter<SubscriptionViewHolder>() {
        @Override
        public SubscriptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subscription, parent, false);
            return new SubscriptionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SubscriptionViewHolder viewHolder, int position) {
            DataSet data = subscriptions.get(position);
            viewHolder.subscriptionCheckbox.setText(data.getTopic());
            viewHolder.subscriptionCheckbox.setChecked(data.isSubscribed());
        }

        @Override
        public int getItemCount() {
            return subscriptions.size();
        }
    };

    public SubscriptionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RecyclerView subscriptionView = (RecyclerView) getActivity().findViewById(R.id.subscription_manage);
        subscriptionView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        subscriptionView.setLayoutManager(layoutManager);

        //update
        NotificationService.getInstance().addChangeListener(new ChangeListener() {
            @Override
            public void update() {
                updateItems();
            }
        });

        SubscriptionService.getInstance().addChangeListener(new ChangeListener() {
            @Override
            public void update() {
                updateItems();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_subscription, container, false);
    }

    public void updateItems() {
        List<String> subscribedTopics = SubscriptionService.getInstance().getSubscribedTopics();

        subscriptions.clear();
        for (String topic : NotificationService.getInstance().getTopics()) {
            subscriptions.add(new DataSet(topic, subscribedTopics.contains(topic)));
        }
        mAdapter.notifyDataSetChanged();
    }
}
