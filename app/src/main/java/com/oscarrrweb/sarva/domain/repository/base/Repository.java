package com.oscarrrweb.sarva.domain.repository.base;

import com.oscarrrweb.sarva.domain.repository.exception.RepositoryDeleteException;
import com.oscarrrweb.sarva.domain.repository.exception.RepositoryInsertException;
import com.oscarrrweb.sarva.domain.repository.exception.RepositoryQueryException;
import com.oscarrrweb.sarva.domain.repository.exception.RepositoryUpdateException;

import java.util.List;

/**
 * Provides the contract for basic database functions. The POJO (Plain Old Java Object)
 * referenced can be explained in more detail
 * <a href="https://docs.oracle.com/cd/E13171_01/alsb/docs25/userguide/pojo.html" target="_blank">here</a>.
 * <p>
 * Note that conversions will be required in the database layer to convert from a database column
 * type to a Java primitive and/or object type (e.g. String to {@link java.util.Date} and
 * vice versa).
 *
 * @param <M> The POJO model type
 * @author Johnny Spence
 * @version 1.0.0
 */
public interface Repository<M> {

    /**
     * Performs an INSERT query.
     *
     * @param model The POJO object containing the fields (member variables) and corresponding
     *              model values to insert
     * @return      The AUTO_INCREMENT ID of the inserted row
     * @throws RepositoryInsertException if model parameter null or an error occurs while
     *              inserting the model
     */
    int insert(M model) throws RepositoryInsertException;

    /**
     * Performs an UPDATE query.
     *
     * @param model The POJO object containing the fields (member variables) and corresponding
     *              model values, including the ID and/or UUID of the model, to update
     * @return      The number of rows successfully updated, may be zero if no model values
     *              were changed
     * @throws      RepositoryUpdateException if model parameter null or an error occurs while
     *              updating the model
     */
    int update(M model) throws RepositoryUpdateException;

    /**
     * Performs a DELETE query.
     *
     * @param model The POJO object containing the ID and/or UUID of the model to delete
     * @return      The number of rows successfully deleted, may be zero if model does not exist
     * @throws      RepositoryDeleteException if model parameter null or an error occurs while
     *              deleting the model
     */
    int delete(M model) throws RepositoryDeleteException;

    /**
     * Performs a DELETE query.
     *
     * @param uuid  The model UUID to delete
     * @return      The number of rows successfully deleted, may be zero if model does not exist
     * @throws      RepositoryDeleteException if model parameter null or an error occurs while
     *              deleting the model
     */
    int delete(String uuid) throws RepositoryDeleteException;

    /**
     * Performs a DELETE query.
     *
     * @param id    The model ID to delete
     * @return      The number of rows successfully deleted, may be zero if model does not exist
     * @throws      RepositoryDeleteException if model parameter null or an error occurs while
     *              deleting the model
     */
    int delete(int id) throws RepositoryDeleteException;

    /**
     * Performs a DELETE query on one or more models.
     *
     * @param models    The {@link List} of POJO objects containing the ID and/or UUID of the model
     *                  to delete
     * @return          The number of rows successfully deleted, may be zero if model(s) do
     *                  not exist
     * @throws          RepositoryDeleteException if models parameter null or an error occurs while
     *                  deleting the model(s)
     */
    int delete(List<M> models) throws RepositoryDeleteException;

    /**
     * Performs a SELECT query returning all model rows.
     *
     * @return  {@link List} of all rows as POJO objects
     * @throws  RepositoryQueryException if an error occurs while querying the models
     */
    List<M> getAll() throws RepositoryQueryException;

    /**
     * Performs a SELECT query returning a single model row by ID.
     *
     * @param id    The model ID
     * @return      The model row as POJO object
     * @throws      RepositoryQueryException if an error occurs while querying the models
     */
    M getById(int id) throws RepositoryQueryException;

    /**
     * Performs a SELECT query returning a single model row by UUID.
     *
     * @param uuid  The model UUID
     * @return      The model row as POJO object
     * @throws      RepositoryQueryException if an error occurs while querying the models
     */
    M getByUuid(String uuid) throws RepositoryQueryException;
}
