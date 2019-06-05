package com.oscarrrweb.sarva.data.entity.base;

import android.util.Base64;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.oscarrrweb.sarva.data.utils.DateUtils;
import com.oscarrrweb.sarva.data.utils.UuidUtils;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import timber.log.Timber;

/**
 * The basic building block for creating POJO objects that serve as database entities
 * (via {@link androidx.room.Room} database) and/or for transfer across network
 * API calls (via {@link Gson}).
 * <p>
 * This abstract class contains the Entity common fields with getters and setters. Also, static
 * utility functions to create a single Entity object or Entity {@link List}s on the fly from JSON,
 * plus options converting to JSON for network transfer are provided.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
@androidx.room.Entity
abstract public class Entity implements Serializable {

    /**
     * The entity ID, annotated to designate this as the primary key field in the
     * {@link androidx.room.Room} database.
     */
    @PrimaryKey(autoGenerate = true)
    private int id;

    /**
     * The entity UUID. While the entity contains a primary key, entities should be accessed
     * by UUID if persisting across networks.
     */
    private String uuid;

    /**
     * The timestamp of entity creation, {@link androidx.room.Room} annotated to
     * specify the database column name and {@link Gson} annotated to specify key name when
     * serializing Entity as JSON.
     */
    @ColumnInfo(name = "created_at")
    @SerializedName("created_at")
    private Date createdAt;

    /**
     * The timestamp of entity creation, Room annotated to specify the database column name and
     * Gson annotated to specify key name when serializing Entity as JSON.
     */
    @ColumnInfo(name = "updated_at")
    @SerializedName("updated_at")
    private Date updatedAt;

    /**
     * Flag if the id (primary key) member field should be serialized. Generally, if the UUID
     * is used as the primary key reference, this would be set to false.
     * {@link androidx.room.Room} annotated to specify this is not a database column.
     */
    @Ignore
    private transient boolean mHasSerializedId = false;

    /**
     * Flag to format output JSON in readable format. Useful for debugging but not for data
     * transfer. {@link androidx.room.Room} annotated to specify this is not a
     * database column.
     */
    @Ignore
    private transient boolean mUsePrettyPrint = false;

    /**
     * Flag to include null values in serialized JSON. {@link androidx.room.Room}
     * annotated to specify this is not a database column.
     */
    @Ignore
    private transient boolean mUseSerializeNulls = false;

    /**
     * The {@link #touch()} method should generally be called on the Entity to add or update the
     * timestamp fields. In certain cases, however, this may not be desired. If this set to false,
     * then subsequent calls to <code>touch()</code> will not update the timestamps.
     * {@link androidx.room.Room} annotated to specify this is not a database column.
     */
    @Ignore
    private transient boolean mDoUpdateTimestamps = true;

    /**
     * Creates a deserializer for boolean values typically set as 0 or 1 within JSON and
     * converting those to primitive boolean type. When configured with a {@link Gson}
     * deserializer, will automatically convert JSON fields into their respective
     * fields of the Entity.
     */
    public static class BooleanDeserializer implements JsonDeserializer<Boolean> {

        /**
         * Converts a JSON element boolean to a primitive boolean value in it's corresponding
         * Entity member field.
         *
         * @param json      The Gson JsonElement element to deserialize
         * @param typeOfT   Type of object to deserialize to (ignored)
         * @param context   (Ignored within overrided method)
         * @return          The Boolean deserialized value from a JSON element
         * @throws JsonParseException if JsonElement contains an invalid value to be parsed.
         */
        @Override
        public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            int boolVal;
            try {
                boolVal = json.getAsInt();
            } catch (NumberFormatException e) {
                // Sometimes we'll catch booleans as true/false
                // just return boolean values
                return json.getAsBoolean();
            }

            return boolVal > 0;
        }
    }

    /**
     * Serializes an Entity member boolean field to a 0 or 1 for use to store in a database or
     * in JSON across a network call. When configured with for {@link Gson#toJson(Object)}
     * deserializer, will automatically convert Entity fields into their respective
     * fields of the resulting JSON.
     */
    public static class BooleanSerializer implements JsonSerializer<Boolean> {

        /**
         * Converts a Java Boolean type to 0 or 1 for use in database or JSON output.
         *
         * @param boolVal       The Java boolean value to convert
         * @param typeOfSrc     The type of source object (ignored)
         * @param context       (Ignored within overrided method)
         * @return              The Gson JsonElement containing the primitive int value of boolean,
         *                      0 or 1
         */
        @Override
        public JsonElement serialize(Boolean boolVal, Type typeOfSrc, JsonSerializationContext context) {
            int intVal = boolVal ? 1 : 0;
            return new JsonPrimitive(intVal);
        }
    }

    /**
     * Creates a deserializer for base64 encoded strings (typically files) within JSON and
     * converting those to a Java byte array. When configured with a {@link Gson}
     * deserializer, will automatically convert JSON fields into their respective
     * fields of the Entity.
     * <p>
     * NOTE: Android Base64 used for backward compatibility (API 23+)
     * java.util.Base64 is Java 8 an API 26+
     */
    public static class ByteArrayDeserializer implements JsonDeserializer<byte[]> {

        /**
         * Converts a JSON element base64 string to a Java byte array in it's corresponding
         * Entity member field.
         *
         * @param json      The Gson JsonElement element to deserialize
         * @param typeOfT   Type of object to deserialize to (ignored)
         * @param context   (Ignored within overrided method)
         * @return          The Boolean deserialized value from a JSON element
         * @throws JsonParseException if JsonElement contains an invalid value to be parsed.
         */
        @Override
        public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            String base64Str = json.getAsString();
            if (base64Str == null || base64Str.equals("")) {
                return new byte[]{};
            }

            return Base64.decode(base64Str, Base64.DEFAULT);
        }
    }

    /**
     * Serializes an Entity member byte array field to a base64 encoded string for use to
     * store in a database or in JSON across a network call. When configured with for
     * {@link Gson#toJson(Object)} deserializer, will automatically convert Entity
     * fields into their respective fields of the resulting JSON.
     * <p>
     * NOTE: Android Base64 used for backward compatibility (API 23+)
     * java.util.Base64 is Java 8 an API 26+
     */
    public static class ByteArraySerializer implements JsonSerializer<byte[]> {

        /**
         * Converts a Java byte array to base64 encoded string for use in database or JSON output.
         *
         * @param bytes         The Java byte array value to convert
         * @param typeOfSrc     The type of source object (ignored)
         * @param context       (Ignored within overrided method)
         * @return              The Gson JsonElement containing the base64 encoded string
         */
        @Override
        public JsonElement serialize(byte[] bytes, Type typeOfSrc, JsonSerializationContext context) {
            if (bytes == null) {
                return null;
            }
            String base64Str = Base64.encodeToString(bytes, Base64.DEFAULT);
            return new JsonPrimitive(base64Str);
        }
    }

    /**
     * Creates a deserializer for SQL formatted date strings (yyyy-mm-dd) within JSON and
     * converting those to a Java {@link Date}. When configured with a {@link Gson}
     * deserializer, will automatically convert JSON fields into their respective
     * fields of the Entity.
     */
    public static class DateDeserializer implements JsonDeserializer<Date> {

        /**
         * Converts a JSON element SQL date string to a Java Date in it's corresponding
         * Entity member field.
         *
         * @param json      The Gson JsonElement element to deserialize
         * @param typeOfT   Type of object to deserialize to (ignored)
         * @param context   (Ignored within overrided method)
         * @return          The Boolean deserialized value from a JSON element
         * @throws JsonParseException if JsonElement contains an invalid value to be parsed.
         */
        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            String dateStr = json.getAsString();
            try {
                return DateUtils.sqlStringToDate(dateStr);
            } catch (ParseException e) {
                Timber.e(e.getMessage());
                return null;
            }
        }
    }

    /**
     * Serializes an Entity member Date field to an SQL string for use in storage
     * in a database or in JSON across a network call. When configured with for
     * {@link Gson#toJson(Object)} deserializer, will automatically convert Entity
     * fields into their respective fields of the resulting JSON.
     * <p>
     * NOTE: Android Base64 used for backward compatibility (API 23+)
     * java.util.Base64 is Java 8 an API 26+
     */
    public static class DateSerializer implements JsonSerializer<Date> {

        /**
         * Converts a Java Date to an SQL string in format yyyy-dd-mm for use in database or
         * JSON output.
         *
         * @param date         The Java Date value to convert
         * @param typeOfSrc    The type of source object (ignored)
         * @param context      (Ignored within overrided method)
         * @return             The Gson JsonElement containing the base64 encoded string
         */
        @Override
        public JsonElement serialize(Date date, Type typeOfSrc, JsonSerializationContext context) {
            String dateStr = DateUtils.dateToSqlString(date);
            return new JsonPrimitive(dateStr);
        }
    }


    /**
     * Constructor
     */
    protected Entity() {}

    /**
     * Returns the database table name for this entity. To be defined in subclass.
     *
     * @return The table name
     */
    abstract public String getTableName();

    /**
     * Returns the entity ID, typically generated by the underlying database.
     *
     * @return The ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the entity ID.
     *
     * @param id    The ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the entity UUID.
     *
     * @return The UUID
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * An empty "setter" that will generate the entity UUID with a proper random value. If a
     * UUID has already been set for this Entity then the call will be ignored.
     *
     */
    public void setUuid() {
        if (uuid == null) {
            uuid = UuidUtils.uuid();
        }
    }

    /**
     * Sets the entity UUID. An empty String passed as a parameter will generate a
     * valid UUID for the entity. However, note that the value can be set to null.
     * <p>
     * Note the <code>uuid</code> parameter is validated and will throw an
     * IllegalArgumentException if not a valid UUID.
     *
     * @param uuid  The UUID
     * @throws      IllegalArgumentException if String parameter an invalid UUID.
     */
    public void setUuid(String uuid) {
        if (uuid == null) {
            this.uuid = uuid;
            return;
        } else if (uuid.equals("")) {
            setUuid();
            return;
        } else if (!uuid.matches("^([0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12})$")) {
            throw new IllegalArgumentException("Parameter [uuid] not in correct UUID format");
        }

        this.uuid = uuid;
    }

    /**
     * Returns the entity time of creation <code>Date</code> timestamp.
     *
     * @return The created at timestamp
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the entity time of creation <code>Date</code> timestamp.
     *
     * @param createdAt The timestamp
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Returns the entity time of last update <code>Date</code> timestamp.
     *
     * @return The updated at timestamp
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the entity time of last update <code>Date</code> timestamp.
     *
     * @param updatedAt The timestamp
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Override to provide a more thorough check if two entities are equal. Subclasses
     * should also provide a similar override.
     *
     * @param o     The object to compare
     * @return      True if object parameter is equal to this entity object
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity)) return false;

        Entity that = (Entity) o;

        if (id != that.id) return false;
        if ((uuid == null && that.uuid != null) ||
                (uuid != null && !uuid.equals(that.uuid)))
            return false;
        if ((createdAt == null && that.createdAt != null) ||
                (createdAt != null && !createdAt.equals(that.createdAt)))
            return false;
        return (updatedAt == null && that.updatedAt == null) ||
                (updatedAt != null && updatedAt.equals(that.updatedAt));
    }

    /**
     * Override to provide a more thorough hash code for use with <code>equals()</code>. Subclasses
     * should also provide a similar override.
     *
     * @return This entity object's hash code
     */
    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + uuid.hashCode();
        result = 31 * result + createdAt.hashCode();
        result = 31 * result + updatedAt.hashCode();
        return result;
    }

    /**
     * Converts a JSON string to an Entity subclass object via {@link Gson}. The JSON must
     * contain keys corresponding to the member variable names and/or the member variables
     * annotated with <code>@SerializedName([name])</code>.
     * <p>
     * NOTE: this method will return <code>null</code> if an Exception occurs while
     * parsing the JSON string.
     *
     * @param json          The JSON string to parse into object
     * @param entityClass   The Class<? extends Entity> to convert the JSON to
     * @return              An Entity object, can be cast to subclass object
     * @see                 Gson
     */
    public static Entity fromJson(String json, Class<? extends Entity> entityClass) {
        try {
            return getGson().fromJson(json, entityClass);
        } catch (JsonSyntaxException e) {
            Timber.e(e);
            return null;
        }
    }

    /**
     * Converts a Gson {@link JsonElement} object to an Entity subclass object. The
     * object must contain keys corresponding to the member variable names and/or the
     * member variables annotated with <code>@SerializedName([name])</code>.
     * <p>
     * NOTE: this method will return <code>null</code> if an Exception occurs while
     * parsing the JsonElement.
     *
     * @param element       The JsonElement object to parse into object
     * @param entityClass   The Class<? extends Entity> to convert the JSON to
     * @return              An Entity object, can be cast to subclass object
     * @see                 Gson
     * @see                 JsonElement
     */
    public static Entity fromJson(JsonElement element, Class<? extends Entity> entityClass) {
        try {
            return getGson().fromJson(element, entityClass);
        } catch (JsonSyntaxException e) {
            Timber.e(e);
            return null;
        }
    }

    /**
     * Converts a Gson {@link JsonObject} object to an Entity subclass object. The
     * object must contain keys corresponding to the member variable names and/or the
     * member variables annotated with <code>@SerializedName([name])</code>.
     * <p>
     * NOTE: this method will return <code>null</code> if an Exception occurs while
     * parsing the JsonObject.
     *
     * @param jsonObject    The JsonObject object to parse into object
     * @param entityClass   The Class<? extends Entity> to convert the JSON to
     * @return              An Entity object, can be cast to subclass object
     * @see                 Gson
     * @see                 JsonObject
     */
    public static Entity fromJson(JsonObject jsonObject, Class<? extends Entity> entityClass) {
        try {
            return getGson().fromJson(jsonObject, entityClass);
        } catch (JsonSyntaxException e) {
            Timber.e(e);
            return null;
        }
    }

    /**
     * Converts a JSON string representing an array to a {@link List} of Entity subclass objects
     * via {@link Gson}. The JSON objects of the array must contain keys corresponding to the
     * member variable names and/or the  member variables annotated with
     * <code>@SerializedName([name])</code>.
     * <p>
     * NOTE: this method will return <code>null</code> if an Exception occurs while
     * parsing the JSON string or if the string does not represent an array.
     *
     * @param json          The JSON string to parse into an array of objects
     * @param entityClass   The Class<? extends Entity> to convert the JSON array elements to
     * @return              The List of Entity objects, NOT the subclassed Entity
     * @see                 Gson
     */
    public static List<Entity> fromJsonArray(String json, Class<? extends Entity> entityClass) {
        JsonParser parser = new JsonParser();
        JsonArray elements;

        try {
            elements = parser.parse(json).getAsJsonArray();
            return Entity.fromJsonArray(elements, entityClass);
        } catch (JsonSyntaxException | IllegalStateException e) {
            Timber.e(e);
            return null;
        }
    }

    /**
     * Converts a Gson {@link JsonArray} object to a {@link List} of Entity subclass objects.
     * The JSON objects of the array must contain keys corresponding to the member variable
     * names and/or the  member variables annotated with <code>@SerializedName([name])</code>.
     * <p>
     * NOTE: this method will return <code>null</code> if an Exception occurs while
     * parsing the JSON string or if the string does not represent an array.
     *
     * @param elements      The JsonArray to parse into an array of objects
     * @param entityClass   The Class<? extends Entity> to convert the JSON array elements to
     * @return              The List of Entity objects, NOT the subclassed Entity
     * @see                 Gson
     * @see                 JsonArray
     */
    public static List<Entity> fromJsonArray(JsonArray elements, Class<? extends Entity> entityClass) {
        if (elements == null) {
            return null;
        }

        ArrayList<Entity> collection = new ArrayList<>();
        Gson gson = getGson();

        try {
            for (JsonElement element : elements) {
                collection.add(gson.fromJson(element, entityClass));
            }
        } catch (JsonSyntaxException | IllegalStateException e) {
            Timber.e(e);
            return null;
        }

        return collection;
    }

    /**
     * Returns a {@link Gson} object configured additionally with the DateDeserializer,
     * BooleanDeserializer and ByteArrayDeserializer to parse JSON into their respective types.
     *
     * @return  The Gson object
     * @see     Gson
     * @see     Entity.DateDeserializer
     * @see     Entity.BooleanDeserializer
     * @see     Entity.ByteArrayDeserializer
     */
    public static Gson getGson() {
        GsonBuilder builder = new GsonBuilder();

        // Convert SQL YYYY-MM-DD( HH:mm:ss) strings to Date objects
        builder.registerTypeAdapter(Date.class, new DateDeserializer());

        // Convert 0 or 1 to boolean
        builder.registerTypeAdapter(boolean.class, new BooleanDeserializer());

        // Convert BLOB strings to byte arrays
        builder.registerTypeAdapter(byte[].class, new ByteArrayDeserializer());

        return builder.create();
    }

    /**
     * Updates the <code>createdAt</code> timestamp to the current date/time, if Entity is new
     * with no id set, and/or updates the <code>updatedAt</code> timestamp to the current
     * date/time.
     */
    public void touch() {
        if ( ! mDoUpdateTimestamps) {
            return;
        }

        Date now = DateUtils.currentTimestamp();
        if (getId() == 0 || createdAt == null) {
            setCreatedAt(now);
        }
        setUpdatedAt(now);
    }

    /**
     * When set to true, this will will not update the <code>createdAt</code> or
     * <code>updatedAt</code> timestamps on subsequent calls to {@link Entity#touch()}
     *
     * @param doUpdateTimestamps True to allow updates to Entity timestamp fields, false to disable
     *                           updating
     */
    public void doUpdateTimestamps(boolean doUpdateTimestamps) {
        mDoUpdateTimestamps = doUpdateTimestamps;
    }

    /**
     * Converts this Entity to a JSON string with {@link Gson} allowing the following
     * configurations:
     * <ul>
     * <li>
     * <b>Custom Serializer:</b> A custom {@link JsonSerializer} for the Entity subclass can be
     * configured by overriding {@link Entity#getSerializer()} in the subclass.
     * </li>
     * <li>
     * <b>Field exclusion:</b> {@link Entity#getExclusionStrategies()} can be overridden to add
     * one or more {@link ExclusionStrategy} to skip fields in the JSON output.
     * </li>
     * <li>
     * <b>Field naming:</b> {@link Entity#getFieldNamingPolicy()} can be overridden to add a
     * {@link FieldNamingPolicy} convention for naming keys in the JSON output.
     * </li>
     * <li>
     * <b>Field naming:</b> {@link Entity#getFieldNamingStrategy()} can be overridden to add a
     * {@link FieldNamingStrategy} for naming keys not supported by Java convention. Similar to
     * above FieldNamingPolicy.
     * </li>
     * <li>
     * <b>Serialize <code>null</code> values:</b> {@link Entity#setSerializeNulls(boolean)} set to
     * true will keep JSON output fields with null values. Otherwise the key/values will be left
     * out of the JSON output which is the default behavior.
     * </li>
     * <li>
     * <b>Pretty print:</b> {@link Entity#setPrettyPrint(boolean)} set to true will output the
     * JSON in readable indented format. Useful for debugging but increases JSON size in
     * production. Pretty print is disabled by default.
     * </li>
     * In addition, the following Java types will be converted within the JSON:
     * <ul>
     * <li>Date -> String formatted "yyyy-mm-dd" or "yyyy-mm-dd hh:mm:ss"</li>
     * <li>boolean -> 0 or 1</li>
     * <li>byte[] -> base64 encoded String</li>
     * </ul>
     *
     * @return The resulting JSON string of this Entity
     */
    public String toJson() {
        GsonBuilder builder = new GsonBuilder();

        if (mUsePrettyPrint) {
            builder.setPrettyPrinting();
        }

        if (mUseSerializeNulls) {
            builder.serializeNulls();
        }

        List<ExclusionStrategy> strategyList = this.getExclusionStrategies();
        if (strategyList != null && strategyList.size() > 0) {
            ExclusionStrategy[] exclusionStrategies = new ExclusionStrategy[strategyList.size()];
            exclusionStrategies = strategyList.toArray(exclusionStrategies);
            builder.setExclusionStrategies(exclusionStrategies);
        }

        FieldNamingPolicy fieldNamingPolicy = this.getFieldNamingPolicy();
        if (fieldNamingPolicy != null) {
            builder.setFieldNamingPolicy(fieldNamingPolicy);
        }

        FieldNamingStrategy fieldNamingStrategy = this.getFieldNamingStrategy();
        if (fieldNamingStrategy != null) {
            builder.setFieldNamingStrategy(fieldNamingStrategy);
        }

        // Convert Date fields to string
        builder.registerTypeAdapter(Date.class, new DateSerializer());

        // Convert boolean to 0 and 1
        builder.registerTypeAdapter(Boolean.class, new BooleanSerializer());

        // Convert byte arrays to string
        builder.registerTypeAdapter(byte[].class, new ByteArraySerializer());

        JsonSerializer<? extends Entity> serializer = this.getSerializer();
        if (serializer != null) {
            builder.registerTypeAdapter(this.getClass(), serializer);
        }

        Gson gson = builder.create();
        return gson.toJson(this);
    }

    /**
     * Converts this Entity to a JSON string with option for pretty-print readable output.
     *
     * @param usePrettyPrint    True to output JSON in readable format
     * @return                  The resulting JSON string of this Entity with pretty print option
     * @see                     Entity#toJson() for configuration options
     */
    public String toJson(boolean usePrettyPrint) {
        boolean switchPrintFlag = ! mUsePrettyPrint;
        mUsePrettyPrint = usePrettyPrint;
        String jsonStr = toJson();

        if (switchPrintFlag) {
            mUsePrettyPrint = false;
        }

        return jsonStr;
    }

    /**
     * Returns a String representation of this Entity. Very similar in output to
     * {@link Entity#toJson(boolean)} by formatting in pretty-print readable format <b>but
     * also includes the <code>id</code> field, even if not serialized as a part of this
     * Entity</b>.
     *
     * @return The string representing this Entity
     */
    @Override
    public String toString() {
        boolean switchPrintFlag = ! mUsePrettyPrint;
        boolean switchSerializedId = ! mHasSerializedId;
        mHasSerializedId = true;

        String json = this.toJson(true);

        if (switchPrintFlag) {
            mUsePrettyPrint = false;
        }
        if (switchSerializedId) {
            mHasSerializedId = false;
        }
        return json;
    }

    /**
     * Returns one or more {@link ExclusionStrategy} to not include Entity fields in JSON output.
     * By default, the <code>id</code> is not included. Subclasses can override this method to
     * include custom exclusions.
     *
     * @return A {@link List} of ExclusionStrategy exclusion strategies
     */
    protected List<ExclusionStrategy> getExclusionStrategies() {
        ArrayList<ExclusionStrategy> strategies = new ArrayList<>(0);
        if (mHasSerializedId) {
            return strategies;
        }

        ExclusionStrategy strategy = new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getName().equals("id");
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        };

        strategies.add(strategy);
        return strategies;
    }

    /**
     * Returns the {@link FieldNamingPolicy} for this Entity. May be overridden in subclass
     * to rename JSON fields that may not correspond to Java naming conventions (e.g SomeField
     * -> some-field).
     * <p>
     * <b>NOTE: will not override any Entity fields annotated with @SerializedName.</b>
     *
     * @return A FieldNamingPolicy for naming JSON fields
     * @see FieldNamingPolicy
     */
    protected FieldNamingPolicy getFieldNamingPolicy() {
        return null;
    }

    /**
     * Returns the {@link FieldNamingStrategy} for this Entity. May be overridden in subclass
     * to rename JSON fields that may not correspond to Java naming conventions (e.g SomeField
     * -> some-field).
     * <p>
     * <b>NOTE: will not override any Entity fields annotated with @SerializedName.</b>
     *
     * @return A FieldNamingStrategy for naming JSON fields
     * @see FieldNamingStrategy
     */
    protected FieldNamingStrategy getFieldNamingStrategy() {
        return null;
    }

    /**
     * Returns a custom {@link JsonSerializer} for the subclassed Entity.
     *
     * @return The JsonSerializer
     */
    protected JsonSerializer<? extends Entity> getSerializer() {
        return null;
    }

    /**
     * Sets the flag to include the <code>id</code> field in the Entity JSON output. Since
     * <code>id</code> is typically a local primary key field, this is set to false by
     * default to not include in JSON output.
     *
     * @param hasSerializedId   True to serialize the <code>id</code> field
     */
    protected void hasSerializedId(boolean hasSerializedId) {
        this.mHasSerializedId = hasSerializedId;
    }

    /**
     * Sets the flag to render this Entity in pretty-print readable format, useful for
     * debugging, bad for transferring across networks.
     *
     * @param usePrettyPrint    True to render JSON in pretty-print
     */
    protected void setPrettyPrint(boolean usePrettyPrint) {
        mUsePrettyPrint = usePrettyPrint;
    }

    /**
     * Sets the flag to include serialized <code>null</code> values in JSON output. Since including
     * <code>null</code> in JSON increases it's size, the flag is set to false by default.
     *
     * @param useSerializeNulls     True to include serialized <code>null</code> values
     */
    protected void setSerializeNulls(boolean useSerializeNulls) {
        mUseSerializeNulls = useSerializeNulls;
    }
}