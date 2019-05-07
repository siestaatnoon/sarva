package com.oscarrrweb.sarva.data.storage.utils;

import android.content.Context;
import androidx.annotation.NonNull;

import com.oscarrrweb.sarva.data.Constants;
import com.oscarrrweb.sarva.data.settings.GeneralSettingsManager;

/**
 * Utility class for the <code>storage</code> package.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class DatabaseUtils {

    /**
     * Returns a unique name for the application database based on the user name.
     *
     * @param context   The Android application context
     * @return          The unique database name
     */
    public static String getCurrentUserDatabaseName(@NonNull Context context) {
        GeneralSettingsManager settingsManager = new GeneralSettingsManager(context);
        String username = settingsManager.getUsername();
        String nameAppend = username == null || username.equals("") ? "" : "_" + username.toLowerCase();
        return String.format(Constants.APP_DATABASE, nameAppend);
    }
}
