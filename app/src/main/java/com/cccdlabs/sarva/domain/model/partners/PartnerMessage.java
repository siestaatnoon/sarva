package com.cccdlabs.sarva.domain.model.partners;

import com.cccdlabs.sarva.data.utils.DateUtils;

import java.util.Date;

/**
 * POJO object used for messaging between devices in the application.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class PartnerMessage {

    /**
     * UUID of user sending the message.
     */
    private String uuid;

    /**
     * User name in device sending the message.
     */
    private String username;

    /**
     * Human-readable name of device sending the message.
     */
    private String deviceName;

    /**
     * The message type contained within the message corresponding to the message function.
     */
    private Mode mode;

    /**
     * Timestamp of the message.
     */
    private Date time;

    /**
     * Enumeration for the message type.
     */
    public enum Mode {

        /**
         * Type used for partner check
         * .
         */
        CHECK("check"),

        /**
         * Type used for device pairing.
         */
        PAIR("pair"),

        /**
         * Type used for device sending a "signal" in a search.
         */
        PING("ping"),

        /**
         * Type used for notifying other devices that device in search mode.
         */
        SEARCH("search");

        private final String value;

        Mode(String value) {
            this.value = value;
        }

        /**
         * Overidden for ease with serialization.
         *
         * @return The enumeration String value
         */
        @Override
        public String toString() {
            return value;
        }

        /**
         * Convenience method to retrieve an enumeration value from a String.
         *
         * @param value The enumeration String value
         * @return      The Mode enumeration value
         */
        public static Mode fromValue(String value) {
            if (value == null) {
                return null;
            }

            try {
                return valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        /**
         * Returns the String enumeration value
         *
         * @return The String value
         */
        public String value() {
            return value;
        }
    }

    /**
     * Default constructor.
     */
    public PartnerMessage() {}

    /**
     * Constructor.
     *
     * @param mode The message type value
     * @see Mode
     */
    public PartnerMessage(Mode mode) {
        this.mode = mode;
        this.time = DateUtils.currentTimestamp();
    }

    /**
     * Returns the message user UUID.
     *
     * @return The UUID
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets the message user UUID.
     *
     * @param uuid The UUID
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
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
     * Returns the message type.
     *
     * @return The message type
     * @see Mode
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * Sets the message type.
     *
     * @param mode The message type
     * @see Mode
     */
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    /**
     * Returns the message timestamp.
     *
     * @return The timestamp
     */
    public Date getTime() {
        return time;
    }

    /**
     * Sets the message timestamp.
     *
     * @param time The timestamp
     */
    public void setTime(Date time) {
        this.time = time;
    }
}
