package de.timonback.notipush.component.notification;


public class Notification {
    private String mDate;
    private String mMessage;
    private String mSource;

    public Notification() {
        // Needed for Firebase
    }

    public Notification(String message, String date, String source) {
        mMessage = message;
        mDate = date;
        mSource = source;
    }

    public String getSource() {
        return mSource;
    }

    public void setSource(String source) {
        mSource = source;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }
}
