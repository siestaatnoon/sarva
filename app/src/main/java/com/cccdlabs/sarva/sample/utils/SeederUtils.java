package com.cccdlabs.sarva.sample.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.cccdlabs.sarva.data.Constants;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class SeederUtils {

    private static final String TAG = SeederUtils.class.getSimpleName();

    public static String getRawResourceString(Context context, int resId) {
        InputStream input;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String resString;

        try {
            input = context.getResources().openRawResource(resId);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }

        try {
            int result = input.read();
            while (result != -1) {
                output.write((byte) result);
                result = input.read();
            }
            resString = output.toString(Constants.CHAR_ENCODING);
            input.close();
            output.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }

        return resString;
    }

    public static String getJsonArrayStringFromMember(String jsonStr, String memberName) {
        if (jsonStr == null || jsonStr.equals("") || memberName == null || memberName.equals("")) {
            return null;
        }

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(jsonStr);
        if (element.isJsonArray()) {
            // method only returns data objects, not arrays of any kind
            return null;
        }

        JsonObject jsonObject = element.getAsJsonObject();
        if (jsonObject.has(memberName)) {
            return jsonObject.getAsJsonArray(memberName).toString();
        }

        return null;
    }
}
