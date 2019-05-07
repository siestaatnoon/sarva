package com.oscarrrweb.sarva.presentation.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.oscarrrweb.sarva.R;
import com.oscarrrweb.sarva.data.settings.GeneralSettingsManager;
import com.oscarrrweb.sarva.domain.settings.GeneralSettings;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceScreen;

public class GeneralPreferencesFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private GeneralSettings mGeneralSettings;

    public GeneralPreferencesFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mGeneralSettings = new GeneralSettingsManager(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.prefs_general);
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();

        for (int i = 0; i < count; i++) {
            Preference preference = prefScreen.getPreference(i);
            String value = sharedPreferences.getString(preference.getKey(), "");

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(value);
                if (prefIndex >= 0) {
                    // Set the summary to that label
                    listPreference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            } else if (preference instanceof EditTextPreference) {
                preference.setSummary(value);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (preference == null) {
            return;
        }

        if (key.equals(mGeneralSettings.getUsername())) {
            String value = sharedPreferences.getString(key, null);
            mGeneralSettings.setUsername(value);
            preference.setSummary(value);
        }
    }
}
