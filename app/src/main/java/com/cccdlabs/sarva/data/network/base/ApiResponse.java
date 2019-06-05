package com.cccdlabs.sarva.data.network.base;

import com.google.gson.JsonObject;
import com.cccdlabs.sarva.data.entity.base.Entity;

import java.util.List;
import java.util.Map;

/**
 * Abstraction for parsing and retrieving the result of an API response.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public interface ApiResponse {

    /**
     * Returns the update count from a PUT request or delete count from DELETE request. Will
     * return -1 for all other request types.
     *
     * @return int The update or delete count
     */
    int getCount();

    /**
     * Returns a {@link Entity} object parsed from a GET response by ID or UUID.
     *
     * @return The Entity object
     */
    Entity getEntity();

    /**
     * Returns a {@link List} of {@link Entity} objects parsed from a GET response by query
     * parameters.
     *
     * @return The List<Entity> collection
     */
    List<Entity> getEntityCollection();

    /**
     * Returns the error message if a server-side error occurs, null otherwise.
     *
     * @return The server error message or null if no error occurred
     */
    String getError();

    /**
     * Returns the response headers as a {@link Map}.
     *
     * @return The response headers as a Map
     */
    Map<String, String> getHeaders();

    /**
     * Returns the server response as raw JSON in a {@link JsonObject}.
     *
     * @return The server response JSON
     */
    JsonObject getJsonResult();

    /**
     * Returns the HTTP status code for the response.
     *
     * @return The status code
     */
    int getStatusCode();

    /**
     * Returns an {@link Entity} object utilized as an encapsulation for a server error
     * that has occurred. Returns null if no server error occurs.
     *
     * @return The Entity encapsulating the server error
     */
    Entity getErrorEntity();

    /**
     * Returns true if the server response is a "success" generally meaning a 200 response
     * code is received.
     *
     * @return True is server response a success, false otherwise
     */
    boolean isSuccess();
}
