package com.cccdlabs.sarva.presentation.model.partners;

import com.cccdlabs.sarva.presentation.model.base.UiModel;

public class PartnerUiModel extends UiModel {

    /**
     * Name of device user.
     */
    private String username;

    /**
     * Human readable name of device of user.
     */
    private String deviceName;

    /**
     * True if user currently enabled for search by this device. False otherwise.
     */
    private boolean isActive;

    /**
     * Extra data field to store distance a user is (Meters) from this device in search mode.
     * Not a database field or serialized for data transfer.
     */
    private double distance;

    /**
     * Extra data field to store accuracy of distance measurement from <code>distance</code>
     * field. Not a database field or serialized for data transfer.
     */
    private int accuracy;

    /**
     * Extra data field to store the Bluetooth BLE RSSI value of a user in search mode. Not a
     * database field or serialized for data transfer.
     */
    private int rssi;

    /**
     * Extra data field to store the Bluetooth BLE TX power value of a user in search mode. Not a
     * database field or serialized for data transfer.
     */
    private int txPower;


    /**
     * Returns the name of the user connected to this device.
     *
     * @return The user name
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the name of the user connected to this device.
     *
     * @param username The device user name
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the human readable device name.
     *
     * @return The device name
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * Sets the human readable device name.
     *
     * @param deviceName The device name
     */
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    /**
     * Returns True if user enabled for search functions. False otherwise.
     *
     * @return True if search enabled, false if not
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Sets true or false if user enabled for search functions.
     *
     * @param active True if user is enabled for search
     */
    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     * Returns the distance a user is in meters of a user connected to this device.
     *
     * @return The distance in meters
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Sets the distance a user is in meters of a user connected to this device.
     *
     * @param distance The distance in meters
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * Returns the accuracy of the distance measurement.
     *
     * @return The accuracy value
     */
    public int getAccuracy() {
        return accuracy;
    }

    /**
     * Sets the accuracy of the distance measurement.
     *
     * @param accuracy The accuracy value
     */
    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    /**
     * Returns the Bluetooth BLE RSSI value of a user connected to this device.
     *
     * @return The RSSI value
     */
    public int getRssi() {
        return rssi;
    }

    /**
     * Sets the Bluetooth BLE RSSI value of a user connected to this device.
     *
     * @param rssi The RSSI value
     */
    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    /**
     * Returns the Bluetooth BLE TX power value of a user connected to this device.
     *
     * @return The TX power value
     */
    public int getTxPower() {
        return txPower;
    }

    /**
     * Sets the Bluetooth BLE TX power value of a user connected to this device.
     *
     * @param txPower The TX power value
     */
    public void setTxPower(int txPower) {
        this.txPower = txPower;
    }
}
