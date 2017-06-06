package de.timonback.notipush.service.messaging;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import de.timonback.notipush.util.Utils;

public class Message {
    private final String mMessage;
    private final String mCategory;
    private final String mHeader;

    private Message(String header, String message, String category) {
        mMessage = message;
        mHeader = header;
        mCategory = category;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getCategory() {
        return mCategory;
    }

    public String getHeader() {
        return mHeader;
    }

    public static class Parser {
        private static final String TAG = Parser.class.getName();
        private static final String MESSAGE_PATH_MESSAGE = "message";
        private static final String MESSAGE_PATH_CATEGORY = "category";
        private static final String MESSAGE_PATH_HEADER = "header";

        private Parser() {
            //private
        }

        public static Message parse(String json) throws IllegalArgumentException {
            JSONObject obj = null;
            try {
                obj = new JSONObject(json);
            } catch (JSONException e) {
                throw new IllegalArgumentException(e);
            }

            String header = parseString(obj, MESSAGE_PATH_HEADER, "no header given");
            String category = parseString(obj, MESSAGE_PATH_CATEGORY, "no category given");
            String message = parseString(obj, MESSAGE_PATH_MESSAGE, "no message given");

            return new Message(header, message, category);
        }

        public static Message parse(Map<String, String> map) {
            String header = Utils.getOrDefault(map, MESSAGE_PATH_HEADER, "no header given");
            String category = Utils.getOrDefault(map, MESSAGE_PATH_CATEGORY, "no category given");
            String message = Utils.getOrDefault(map, MESSAGE_PATH_MESSAGE, "no message given");

            return new Message(header, message, category);
        }

        private static String parseString(JSONObject obj, String key, String defaultValue) {
            try {
                return obj.getString(key);
            } catch (JSONException e) {
                Log.i(TAG, "could not parse " + key + " from " + obj.toString(), e);
            }
            return defaultValue;
        }
    }
}
