package com.oscarrrweb.sarva.presentation.model.base;

import java.util.Date;

/**
 * Base model <a href="https://docs.oracle.com/cd/E13171_01/alsb/docs25/userguide/pojo.html" target="_blank">POJO</a>
 * with common fields for subclassed models. For use in <code>presentation</code> package.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
abstract public class UiModel {

    /**
     * The AUTO_INCREMENT id of the model.
     */
    private int id;

    /**
     * The UUID of the model.
     */
    private String uuid;

    /**
     * The timestamp of model creation
     */
    private Date createdAt;

    /**
     * The timestamp of most recent model update
     */
    private Date updatedAt;


    /**
     * Returns the model ID.
     *
     * @return The ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the model ID. Normally this would be done automatically within the database
     * framework of the application.
     *
     * @param id The model ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the model UUID.
     *
     * @return The model UUID
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets the model UUID.
     *
     * @param uuid The model UUID
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Returns the model time of creation <code>Date</code> timestamp.
     *
     * @return The created at timestamp
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the model time of creation <code>Date</code> timestamp.
     *
     * @param createdAt The timestamp
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Returns the model time of last update <code>Date</code> timestamp.
     *
     * @return The updated at timestamp
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the model time of last update <code>Date</code> timestamp.
     *
     * @param updatedAt The timestamp
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
