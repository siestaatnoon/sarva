package com.oscarrrweb.sarva.utils;

import android.util.MalformedJsonException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

final public class JsonUtils {

    public static JsonElement toJsonElement(String jsonStr) throws MalformedJsonException {
        if (jsonStr == null) {
            return null;
        }

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(jsonStr);
        if (element.isJsonArray()) {
            // method only returns data objects, not arrays of any kind
            return null;
        }

        return element;
    }

    public static JsonObject toJsonObject(String jsonStr) throws MalformedJsonException {
        if (jsonStr == null) {
            return null;
        }

        JsonElement element = toJsonElement(jsonStr);
        return element.getAsJsonObject();
    }
}
