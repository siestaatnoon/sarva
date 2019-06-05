package com.oscarrrweb.sarva.data.p2p.nearby.utils;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.messages.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.oscarrrweb.sarva.data.Constants;
import com.oscarrrweb.sarva.data.entity.base.Entity;
import com.oscarrrweb.sarva.data.settings.GeneralSettingsManager;
import com.oscarrrweb.sarva.data.utils.DateUtils;
import com.oscarrrweb.sarva.domain.model.partners.Partner;
import com.oscarrrweb.sarva.domain.model.partners.PartnerMessage;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Date;

/**
 * Utility class to convert objects between Nearby {@link Message}, {@link PartnerMessage}
 * and {@link Partner} domain model.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
final public class NearbyUtils {

    /**
     * Creates a deserializer for {@link PartnerMessage.Mode} type strings within JSON and
     * converting those to its enum value.
     */
    private static class ModeDeserializer implements JsonDeserializer<PartnerMessage.Mode> {

        /**
         * Converts a JSON element {@link PartnerMessage.Mode} type string to an enum value.
         *
         * @param json      The Gson JsonElement element to deserialize
         * @param typeOfT   Type of object to deserialize to (ignored)
         * @param context   (Ignored within overrided method)
         * @return          The Mode enum deserialized value from a JSON element
         * @throws JsonParseException if JsonElement contains an invalid value to be parsed.
         */
        @Override
        public PartnerMessage.Mode deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            String modeStr = json.getAsString();
            return PartnerMessage.Mode.fromValue(modeStr);
        }
    }

    /**
     * Creates a Nearby {@link Message} for this device to be published to other devices.
     *
     * @param context   The Android application context
     * @param mode      The message type enum
     * @return          The Nearby Message object
     * @see             PartnerMessage.Mode
     */
    public static Message createMessage(@NonNull Context context, @NonNull PartnerMessage.Mode mode) {
        GeneralSettingsManager settings = new GeneralSettingsManager(context);
        PartnerMessage message = new PartnerMessage();
        message.setUuid(settings.getUuid());
        message.setUsername(settings.getUsername());
        message.setDeviceName(getDeviceName());
        message.setMode(mode);
        message.setTime(DateUtils.currentTimestamp());
        return toNearbyMessage(message);
    }

    /**
     * Returns the device brand/manufacturer + model as a string.
     *
     * @return The device brand/manufacturer + model
     */
    public static String getDeviceName() {
        String brand = Build.BRAND == null
                ? Build.MANUFACTURER
                : Build.BRAND;
        return brand + " " + Build.MODEL;
    }

    /**
     * Converts a {@link PartnerMessage} to Nearby {@link Message}.
     *
     * @param message   The PartnerMessage object
     * @return          The Nearby Message object
     */
    public static Message toNearbyMessage(PartnerMessage message) {
        if (message == null) {
            return null;
        }

        return new Message(partnerMessageToJson(message).getBytes(Charset.forName(Constants.CHAR_ENCODING)));
    }

    /**
     * Converts a {@link Partner} domain model to Nearby {@link Message} via a
     * {@link PartnerMessage}.
     *
     * @param model The Partner domain object
     * @param mode  The Mode message type
     * @return      The Nearby Message object
     * @see         PartnerMessage.Mode
     */
    public static Message toNearbyMessage(Partner model, PartnerMessage.Mode mode) {
        if (model == null) {
            return null;
        }

        PartnerMessage message = new PartnerMessage();
        message.setUuid(model.getUuid());
        message.setUsername(model.getUsername());
        message.setDeviceName(model.getDeviceName());
        message.setMode(mode);
        return new Message(partnerMessageToJson(message).getBytes(Charset.forName(Constants.CHAR_ENCODING)));
    }

    /**
     * Converts a {@link Message} to {@link Partner}.
     *
     * @param message   The Nearby Message object
     * @return          The Partner domain object
     */
    public static Partner toPartnerModel(Message message) {
        if (message == null) {
            return null;
        }

        PartnerMessage partnerMessage = NearbyUtils.toPartnerMessage(message);
        return toPartnerModel(partnerMessage);
    }

    /**
     * Converts a {@link PartnerMessage} to {@link Partner}.
     *
     * @param message   The PartnerMessage object
     * @return          The Partner domain object
     */
    public static Partner toPartnerModel(PartnerMessage message) {
        if (message == null) {
            return null;
        }

        Partner model = new Partner();
        model.setUuid(message.getUuid());
        model.setUsername(message.getUsername());
        model.setDeviceName(message.getDeviceName());
        model.setUpdatedAt(message.getTime());
        return model;
    }

    /**
     * Converts a Nearby {@link Message} to {@link PartnerMessage}.
     *
     * @param message   The Nearby Message object
     * @return          The PartnerMessage object
     */
    public static PartnerMessage toPartnerMessage(Message message) {
        if (message == null) {
            return null;
        }

        String content = new String(message.getContent(), Charset.forName(Constants.CHAR_ENCODING));
        return partnerMessageFromJson(content);
    }

    /**
     * Convenience method to extract a PartnerMessage object from a JSON string. Note that
     * if JSON string parameter is malformed or invalid, null will be returned.
     *
     * @param json  The JSON string
     * @return      The PartnerMessage object
     */
    private static PartnerMessage partnerMessageFromJson(String json) {
        if (json == null) {
            return null;
        }

        try {
            return getGson(true).fromJson(json, PartnerMessage.class);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    /**
     * Returns a JSON String representing a PartnerMessage.
     *
     * @param partnerMessage    The PartnerMessage object
     * @return                  The JSON String
     */
    private static String partnerMessageToJson(@NonNull PartnerMessage partnerMessage) {
        return getGson(false).toJson(partnerMessage);
    }

    /**
     * Returns a {@link Gson} object configured additionally with the DateDeserializer,
     * BooleanDeserializer and ModeDeserializer to parse JSON into their respective types.
     *
     * @param isDeserialize True if Gson object used for deserializing object, otherwise for
     *                      serialization from object
     * @return              The Gson object
     * @see                 Gson
     * @see                 Entity.DateDeserializer
     * @see                 Entity.BooleanDeserializer
     * @see                 ModeDeserializer
     */
    private static Gson getGson(boolean isDeserialize) {
        GsonBuilder builder = new GsonBuilder();

        if (isDeserialize) {
            // Convert SQL YYYY-MM-DD( HH:mm:ss) string to Date object
            builder.registerTypeAdapter(Date.class, new Entity.DateDeserializer());

            // Convert 0 or 1 to boolean
            builder.registerTypeAdapter(boolean.class, new Entity.BooleanDeserializer());

            // Convert {@link Mode} enum to String
            builder.registerTypeAdapter(PartnerMessage.Mode.class, new ModeDeserializer());
        } else {
            builder.serializeNulls();
        }

        return builder.create();
    }
}
