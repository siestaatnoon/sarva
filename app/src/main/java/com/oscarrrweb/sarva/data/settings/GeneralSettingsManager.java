package com.oscarrrweb.sarva.data.settings;

import android.content.Context;
import androidx.annotation.NonNull;

import com.oscarrrweb.sarva.R;
import com.oscarrrweb.sarva.data.Constants;
import com.oscarrrweb.sarva.data.settings.base.SettingsManager;
import com.oscarrrweb.sarva.data.utils.UuidUtils;
import com.oscarrrweb.sarva.domain.settings.GeneralSettings;

/**
 * Implementation of the general settings of the application. Note that the UUID and volume
 * settings saved here will have their values set and saved upon accessing them for the first
 * tie via <code>getUuid()</code> and <code>getVolume()</code> respectively.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class GeneralSettingsManager extends SettingsManager implements GeneralSettings {

    /**
     * Key to access UUID setting.
     */
    private final String sKeyUuid;

    /**
     * Key to access username setting.
     */
    private final String sKeyUsername;

    /**
     * Key to access volume setting.
     */
    private final String sKeyVolume;

    /**
     * Constructor.
     *
     * @param context The Android application {@link Context}.
     */
    public GeneralSettingsManager(@NonNull Context context) {
        super(context);
        sKeyUuid = context.getResources().getString(R.string.pref_uuid_key);
        sKeyUsername = context.getResources().getString(R.string.pref_username_key);
        sKeyVolume = context.getResources().getString(R.string.pref_volume_key);
    }

    /**
     * Returns the String key to access the UUID setting.
     *
     * @return The UUID
     */
    @Override
    public String getUuidKey() {
        return sKeyUuid;
    }

    /**
     * Returns the value of the UUID setting. If accessed for the first time, and shared
     * preference does not exist, will generate the UUID, save it in the preference and
     * return the value.
     *
     * @return The UUID value
     */
    @Override
    public String getUuid() {
        String uuid = mSharedPreferences.getString(sKeyUuid, null);
        if (uuid == null) {
            uuid = UuidUtils.uuid();
            setUuid(uuid);
        }

        return uuid;
    }

    /**
     * Sets the UUID value.
     *
     * @param uuid The UUID
     */
    @Override
    public void setUuid(String uuid) {
        mSharedPreferences.edit().putString(sKeyUuid, uuid).apply();
    }

    /**
     * Returns the String key to access the username setting.
     *
     * @return The username key
     */
    @Override
    public String getUsernameKey() {
        return sKeyUsername;
    }

    /**
     * Returns the value of the username setting.
     *
     * @return The username value
     */
    @Override
    public String getUsername() {
        return mSharedPreferences.getString(sKeyUsername, null);
    }

    /**
     * Sets the username setting.
     *
     * @param username The username
     */
    @Override
    public void setUsername(String username) {
        mSharedPreferences.edit().putString(sKeyUsername, username).apply();
    }

    /**
     * Returns the String key to access the search volume setting.
     *
     * @return The search volume key
     */
    @Override
    public String getVolumeKey() {
        return sKeyVolume;
    }

    /**
     * Returns the value of the search volume setting. If accessed for the first time, and shared
     * preference does not exist, a default value will be set for the preference and returned.
     *
     * @return The search volume value
     */
    @Override
    public int getVolume() {
        int volume = mSharedPreferences.getInt(sKeyVolume, -1);
        if (volume == -1) {
            volume = Constants.SEARCH_TONE_VOLUME;
            setVolume(volume);
        }

        return volume;
    }

    /**
     * Sets the search volume setting.
     *
     * @param volume The search volume
     */
    @Override
    public void setVolume(int volume) {
        mSharedPreferences.edit().putInt(sKeyVolume, volume).apply();
    }
}
