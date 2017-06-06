package de.timonback.notipush.service.notification;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class NotificationSettings {
    private static final String NOTIFICATION_TOPIC = "pref_notification_topic";

    private static NotificationSettings instance;
    private Context mContext;

    private NotificationSettings(Context context) {
        mContext = context;
    }

    public static synchronized NotificationSettings getInstance(Context c) {
        if (instance == null) {
            instance = new NotificationSettings(c);
        }
        return instance;
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
}
