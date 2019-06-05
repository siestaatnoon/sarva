package com.cccdlabs.sarva.data.settings.base;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

/**
 * Abstraction to setup the Android {@link SharedPreferences} for use in saving settings by
 * subclasses.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
abstract public class SettingsManager {

    /**
     * The SharedPreferences object.
     */
    protected final SharedPreferences mSharedPreferences;

    /**
     * Constructor.
     *
     * @param context The application {@link Context}
     */
    public SettingsManager(@NonNull Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
}
