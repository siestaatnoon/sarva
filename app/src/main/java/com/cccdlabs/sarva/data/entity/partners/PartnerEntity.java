package com.cccdlabs.sarva.data.entity.partners;

import com.google.gson.annotations.SerializedName;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import static com.cccdlabs.sarva.data.entity.partners.PartnerEntity.TABLE_NAME;

/**
 * POJO object representing a user connecting to this device for pairing and search functions.
 * Used for the {@link androidx.room.Room} database and for serialization with
 * {@link com.google.gson.Gson}. {@link Entity} annotation designates this as a Room entity.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
@Entity(tableName = TABLE_NAME,
        indices = {
                @Index(value = "uuid", unique = true)
        }
)
public class PartnerEntity extends com.cccdlabs.sarva.data.entity.base.Entity {

    /**
     * Database table name for this Entity.
     */
    protected static final String TABLE_NAME = "partners";

    /**
     * Name of device user.
     */
    private String username;

    /**
     * Human readable name of device of user.
     */
    @ColumnInfo(name = "device_name")
    @SerializedName("device_name")
    private String deviceName;

    /**
     * True if user currently enabled for search by this device. False otherwise.
     */
    @ColumnInfo(name = "is_active")
    @SerializedName("is_active")
    private boolean isActive;

    /**
     * Extra data field, true if user is detected on another device. Not a database field or
     * serialized for data transfer.
     */
    private transient boolean isEmitting;

    /**
     * Extra data field to store distance a user is (Meters) from this device in search mode.
     * Not a database field or serialized for data transfer.
     */
    @Ignore
    private transient double distance;

    /**
     * Extra data field to store accuracy of distance measurement from <code>distance</code>
     * field. Not a database field or serialized for data transfer.
     */
    @Ignore
    private transient int accuracy;

    /**
     * Extra data field to store the Bluetooth BLE RSSI value of a user in search mode. Not a
     * database field or serialized for data transfer.
     */
    @Ignore
    private transient int rssi;

    /**
     * Extra data field to store the Bluetooth BLE TX power value of a user in search mode. Not a
     * database field or serialized for data transfer.
     */
    @Ignore
    private transient int txPower;


    /**
     * Returns the database table name used by this object.
     *
     * @return The database table name
     */
    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

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
     * Returns True if user is detected on another device. False otherwise.
     *
     * @return True if user is detected on another device, false if not
     */
    public boolean isEmitting() {
        return isEmitting;
    }

    /**
     * Sets true or false if user is detected on another device.
     *
     * @param emitting True if user is detected on another device
     */
    public void setEmitting(boolean emitting) {
        isEmitting = emitting;
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
