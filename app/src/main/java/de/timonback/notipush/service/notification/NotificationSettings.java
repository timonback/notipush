package de.timonback.notipush.service.notification;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.LinkedList;
import java.util.List;

public class NotificationSettings {
    private static final String NOTIFICATION_TOPIC = "pref_notification_topic";
    private static NotificationSettings instance;
    private Context mContext;
    private List<NotificationSettings.ChangeListener> listeners = new LinkedList<>();
    private NotificationSettings(Context context) {
        mContext = context;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        sharedPref.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                String newValue = sharedPreferences.getString(key, "invalid");
                if (newValue != "invalid") {
                    switch (key) {
                        case NOTIFICATION_TOPIC:
                            for (ChangeListener listener : listeners) {
                                listener.topicChanged(newValue);
                            }
                    }
                }
            }
        });
    }

    public static synchronized NotificationSettings getInstance(Context c) {
        if (instance == null) {
            instance = new NotificationSettings(c);
        }
        return instance;
    }

    public void addChangeListener(NotificationSettings.ChangeListener listener) {
        listeners.add(listener);
    }

    public void removeChangeListener(NotificationSettings.ChangeListener listener) {
        listeners.remove(listener);
    }

    public String getCurrentTopic() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPref.getString(NOTIFICATION_TOPIC, "");
    }

    public void setCurrentTopic(String topic) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        sharedPref.edit()
                .putString(NOTIFICATION_TOPIC, topic)
                .apply();
        sharedPref.getString(NOTIFICATION_TOPIC, "");
    }

    public interface ChangeListener {
        void topicChanged(String newTopic);
    }
}
