package com.cccdlabs.sarva.domain.repository.partners;

import com.cccdlabs.sarva.domain.model.partners.Partner;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryException;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryUpdateException;

/**
 * Abstraction for database functions specific to the {@link Partner} domain model.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public interface PartnerRepo {

    /**
     * Inserts a model to the database or updates the database model if it already exists.
     *
     * @param model The Partner model
     * @return      The inserted or updated partner model
     * @throws      RepositoryException if an error occurs retrieving, inserting or updating
     *              the entity
     */
    Partner sync(Partner model) throws RepositoryException;

    /**
     * Sets a partner row active flag to true.
     *
     * @param uuid  The row UUID
     * @return      True if row was updated, false if not
     * @throws      RepositoryUpdateException if an SQL occurs
     */
    boolean setActive(String uuid) throws RepositoryUpdateException;

    /**
     * Sets all partner rows active flag to false.
     *
     * @throws RepositoryUpdateException if an SQL occurs
     */
    void setAllInactive() throws RepositoryUpdateException;
}
