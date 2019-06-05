package com.cccdlabs.sarva.data.network.retrofit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.cccdlabs.sarva.data.entity.base.Entity;
import com.cccdlabs.sarva.data.network.base.ApiResponse;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Implementation of the {@link ApiResponse} interface utilizing
 * <a href="https://github.com/google/gson" target="_top">Gson</a> to parse a server
 * network response into POJO objects and capturing errors into a format used in this
 * application.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public final class RestResponse implements ApiResponse {

    /**
     * Constant for HTTP OK response, status code 200.
     */
    public static final int HTTP_OK = 200;

    /**
     * Constant for HTTP unauthorized response, status code 400.
     */
    public static final int HTTP_ERROR_UNAUTHORIZED = 401;

    /**
     * Constant for HTTP user lockout response, status code 423.
     */
    public static final int HTTP_ERROR_LOCKOUT = 423;

    /**
     * Constant for HTTP server error, status code 500.
     */
    public static final int HTTP_ERROR_SERVER = 500;

    /**
     * The server response JSON.
     */
    private final JsonObject mJsonResult;

    /**
     * Subclass of {@link Entity} to convert the server response JSON to.
     */
    private final Class<? extends Entity> mEntityClass;

    /**
     * Flag if API call a "success" receiving a flag in the server response.
     */
    private final boolean mIsSuccess;

    /**
     * Error message parsed from server response, if an error occurs.
     */
    private final String mErrorMsg;

    /**
     * The server response headers.
     */
    private final Map<String, String> mHeaders;

    /**
     * The server response status code.
     */
    private final int mStatusCode;


    /**
     * Constructor.
     *
     * @param statusCode    The HTTP status code of the server response
     * @param jsonResult    The JSON response body
     * @param headers       The server response headers
     * @param entityClass   The {@link Entity} subclass to convert the JSON response to
     * @throws              IllegalArgumentException if entityClass parameter not a subclass of
     *                      {@link Entity}
     */
    @SuppressWarnings("unchecked")
    public RestResponse(int statusCode, JsonObject jsonResult, Map<String, String> headers, Type entityClass) {
        try {
            mEntityClass = (Class<? extends Entity>) entityClass;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("entityClass parameter must be Class object of AbstractEntity subclass");
        }

        boolean isSuccess = false;
        String errorMessage = "JSON parse error";
        if (jsonResult != null) {
            try {
                if (jsonResult.has("success")) {
                    isSuccess = jsonResult.get("success").getAsBoolean();
                }

                if (jsonResult.has("message")) {
                    errorMessage = jsonResult.get("message").getAsString();
                }
            } catch (JsonSyntaxException e) {
                Timber.e(e.getMessage());
            }
        }

        mStatusCode = statusCode;
        mJsonResult = jsonResult;
        mIsSuccess = isSuccess;
        mHeaders = headers;
        mErrorMsg = mIsSuccess ? null : errorMessage;
    }

    /**
     * Returns the number of rows updated for a PUT request or number of rows deleted for
     * a DELETE REQUEST.
     *
     * @return The number of rows updated or deleted
     */
    @Override
    public int getCount() {
        if ( ! mIsSuccess) {
            return -1;
        }

        if (mJsonResult.has("count")) {
            try {
                return mJsonResult.get("count").getAsInt();
            } catch (JsonSyntaxException e) {
                Timber.e(e.getMessage());
                return -1;
            }
        }

        return -1;
    }

    /**
     * Returns the {@link Entity} subclass object converted from the server JSON response.
     *
     * @return The Entity object
     */
    @Override
    public Entity getEntity() {
        if ( ! mIsSuccess) {
            return null;
        }

        try {
            if (mJsonResult.has("item")) {
                JsonElement element = mJsonResult.get("item");
                return Entity.fromJson(element, mEntityClass);
            } else {
                return Entity.fromJson(mJsonResult, mEntityClass);
            }
        } catch (JsonSyntaxException e) {
            Timber.e(e.getMessage());
            return null;
        }
    }

    /**
     * Returns a {@link List} of {@link Entity} subclass objects converted from the server
     * JSON response.
     *
     * @return The List of Entity objects
     */
    @Override
    public List<Entity> getEntityCollection() {
        if ( ! mIsSuccess) {
            return null;
        }

        if (mJsonResult.has("items")) {
            try {
                JsonArray elements = mJsonResult.getAsJsonArray("items");
                return Entity.fromJsonArray(elements, mEntityClass);
            } catch (JsonSyntaxException e) {
                Timber.e(e.getMessage());
                return null;
            }
        }

        return null;
    }

    /**
     * If a server error occurs, this will return an {@link Entity} object from the server
     * response JSON, if expected from the server.
     *
     * @return The Entity object
     */
    public Entity getErrorEntity() {
        if (mIsSuccess) {
            return null;
        }

        try {
            return Entity.fromJson(mJsonResult, mEntityClass);
        } catch (JsonSyntaxException e) {
            Timber.e(e.getMessage());
            return null;
        }
    }

    /**
     * Returns the server error message or <code>null</code> if no error occurs.
     *
     * @return The server error message
     */
    @Override
    public String getError() {
        return mErrorMsg;
    }

    /**
     * Returns the server response headers as a {@link Map}.
     *
     * @return The server response headers
     */
    @Override
    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    /**
     * Returns the full JSON server response body as a {@link JsonObject}.
     *
     * @return The server response JSON
     */
    @Override
    public JsonObject getJsonResult() {
        return mJsonResult;
    }

    /**
     * Returns the HTTP response status code.
     *
     * @return The status code
     */
    @Override
    public int getStatusCode() {
        return mStatusCode;
    }

    /**
     * Returns true if the server response a success, false if not.
     *
     * @return True if server response a success, false if not.
     */
    @Override
    public boolean isSuccess() {
        return mIsSuccess;
    }

    /**
     * Overriding method to output a useful capture of this object's member variables.
     *
     * @return A String representing this RestResponse
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("mIsSuccess: ").append(mIsSuccess ? "true" : "false").append("\n");
        buffer.append("mStatusCode: ").append(mStatusCode).append("\n");
        buffer.append("mEntityClass: ").append(mEntityClass.getSimpleName()).append("\n");
        buffer.append("mErrorMsg: ").append(mErrorMsg).append("\n");
        buffer.append("mHeaders: ").append(mHeaders == null ? "null" : mHeaders.toString()).append("\n");
        buffer.append("mJsonResult: ").append(mJsonResult == null ? "null" : mJsonResult.toString());
        return buffer.toString();
    }
}
