package com.oscarrrweb.sarva.data.settings;

import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.NonNull;

import com.oscarrrweb.sarva.R;
import com.oscarrrweb.sarva.data.settings.base.SettingsManager;
import com.oscarrrweb.sarva.domain.settings.DataSettings;

public class DataSettingsManager extends SettingsManager implements DataSettings {

    private final String sKeySyncEnabled;
    private final String sKeySyncOnlyWhileCharging;
    private final String sKeySyncWifiOnly;

    public DataSettingsManager(@NonNull Context context) {
        super(context);
        Resources resources = context.getResources();
        sKeySyncEnabled = resources.getString(R.string.pref_sync_enabled_key);
        sKeySyncOnlyWhileCharging = resources.getString(R.string.pref_sync_only_while_charging_key);
        sKeySyncWifiOnly = resources.getString(R.string.pref_sync_wifi_only_key);
    }

    @Override
    public String getSyncWifiOnlyKey() {
        return sKeySyncWifiOnly;
    }

    @Override
    public boolean isSyncWifiOnly() {
        return mSharedPreferences.getBoolean(sKeySyncWifiOnly, false);
    }

    @Override
    public void setSyncWifiOnly(boolean isSyncWifiOnly) {
        mSharedPreferences.edit().putBoolean(sKeySyncWifiOnly, isSyncWifiOnly).apply();
    }

    @Override
    public String getSyncOnlyWhileChargingKey() {
        return sKeySyncOnlyWhileCharging;
    }

    @Override
    public boolean isSyncOnlyWhileCharging() {
        return mSharedPreferences.getBoolean(sKeySyncOnlyWhileCharging, false);
    }

    @Override
    public void setSyncOnlyWhileCharging(boolean isSyncOnlyWhileCharging) {
        mSharedPreferences.edit().putBoolean(sKeySyncOnlyWhileCharging, isSyncOnlyWhileCharging).apply();
    }

    @Override
    public String getSyncEnabledKey() {
        return sKeySyncEnabled;
    }

    @Override
    public boolean isSyncEnabled() {
        return mSharedPreferences.getBoolean(sKeySyncEnabled, false);
    }

    @Override
    public void setSyncEnabled(boolean isSyncEnabled) {
        mSharedPreferences.edit().putBoolean(sKeySyncEnabled, isSyncEnabled).apply();
    }
}
