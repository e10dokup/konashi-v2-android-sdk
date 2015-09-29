package com.uxxu.konashi.lib.js;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kiryu on 9/25/15.
 */
public class KonashiJsEvent {

    static KonashiJsEvent fromUri(Uri uri) throws JSONException {
        JSONObject data = new JSONObject(uri.getQuery());
        String firstPath = uri.getPathSegments().get(0).replace("/", "");
        return new KonashiJsEvent(uri.getHost(), Integer.parseInt(firstPath, 10), data);
    }

    private final String eventName;
    private final int messageId;
    private final JSONObject data;

    public KonashiJsEvent(String eventName, int messageId, JSONObject data) {
        this.eventName = eventName;
        this.messageId = messageId;
        this.data = data;
    }

    @Override
    public String toString() {
        return String.format("{KonashiJsEvent %s:%d:%s}", eventName, messageId, data.toString());
    }

    public String getEventName() {
        return eventName;
    }

    public int getMessageId() {
        return messageId;
    }

    public String getStringData(String name) {
        try {
            return data.getString(name);
        } catch (JSONException e) {
            return null;
        }
    }

    public Integer getIntData(String name) {
        try {
            return data.getInt(name);
        } catch (JSONException e) {
            return null;
        }
    }

    public Boolean getBooleanData(String name) {
        try {
            return data.getBoolean(name);
        } catch (JSONException e) {
            return null;
        }
    }
}
