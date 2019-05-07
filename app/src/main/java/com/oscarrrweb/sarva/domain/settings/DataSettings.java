package com.oscarrrweb.sarva.domain.settings;

public interface DataSettings {

    String getSyncWifiOnlyKey();

    boolean isSyncWifiOnly();

    void setSyncWifiOnly(boolean isSyncWifiOnly);

    String getSyncOnlyWhileChargingKey();

    boolean isSyncOnlyWhileCharging();

    void setSyncOnlyWhileCharging(boolean isSyncOnlyWhileCharging);

    String getSyncEnabledKey();

    boolean isSyncEnabled();

    void setSyncEnabled(boolean isSyncEnabled);
}
