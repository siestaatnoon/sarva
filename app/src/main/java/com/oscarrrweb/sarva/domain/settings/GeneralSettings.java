package com.oscarrrweb.sarva.domain.settings;

/**
 * Provides the contract, or rather format, for handling application settings. Settings may be
 * divided into one or more sections and an interface should be defined for each. This interface
 * defines generic defaults but an app may include other settings for notifications, sounds, etc.
 * <p>
 * The pattern used per setting is as follows:
 * <ul>
 * <li>get[settingName]Key() - Returns the String key used to save/retrieve value in implementation</li>
 * <li>get[settingName]() - Returns the setting value</li>
 * <li>set[settingName](value) - Sets the setting value</li>
 * </ul>
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public interface GeneralSettings {

    /**
     * Returns the String key to access the username setting.
     *
     * @return The username key
     */
    String getUsernameKey();

    /**
     * Returns the value of the username setting.
     *
     * @return The username value
     */
    String getUsername();

    /**
     * Sets the username setting.
     *
     * @param username The username
     */
    void setUsername(String username);
}
