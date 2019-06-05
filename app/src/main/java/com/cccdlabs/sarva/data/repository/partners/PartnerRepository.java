package com.cccdlabs.sarva.data.repository.partners;

import android.database.sqlite.SQLiteException;

import com.cccdlabs.sarva.data.entity.partners.PartnerEntity;
import com.cccdlabs.sarva.data.mappers.partners.PartnerMapper;
import com.cccdlabs.sarva.data.repository.base.AbstractRepository;
import com.cccdlabs.sarva.data.storage.dao.partners.PartnerDao;
import com.cccdlabs.sarva.domain.model.partners.Partner;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryException;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryQueryException;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryUpdateException;
import com.cccdlabs.sarva.domain.repository.partners.PartnerRepo;

import javax.inject.Inject;

/**
 * Database functions specific for the {@link Partner} domain model also utilizing
 * {@link PartnerEntity}. General database functions are covered in the inherited
 * {@link AbstractRepository}.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class PartnerRepository extends AbstractRepository<PartnerEntity, Partner, PartnerMapper, PartnerDao>
        implements PartnerRepo {

    /**
     * Constructor. Annotated with {@link Inject} for use with Dagger 2 dependency injection.
     */
    @Inject
    public PartnerRepository() {}

    /**
     * Returns true if partner exists from given UUID.
     * <p>
     * NOTE: It's decided to not check if the active flag for the partner row is
     * true since it could theoretically prevent detection in a partner search.
     *
     * @param uuid  The partner UUID
     * @return      True if partner exists and active flag set to true
     * @throws      RepositoryQueryException if an error occurs retrieving the partner row
     */
    public boolean isActive(String uuid) throws RepositoryQueryException {
        if (uuid == null) {
            return false;
        }

        return super.getByUuid(uuid) != null;
    }

    /**
     * Accepts a Partner domain model and will insert it if it doesn't exist or update the database
     * row if it does. If passed in null value or entity UUID null or empty string, will return
     * null. Note that this method will initially set the status of the model to inactive.
     *
     * @param model The Partner domain model object
     * @return      The Partner domain model object with AUTO_INCREMENT id set
     * @throws      RepositoryException if an error occurs retrieving, inserting or updating
     *              the entity
     */
    public Partner sync(Partner model) throws RepositoryException {
        if (model == null || model.getUuid() == null || model.getUuid().equals("")) {
            return null;
        }

        model.setActive(false);
        Partner toCheck = super.getByUuid(model.getUuid());
        if (toCheck == null) {
            int id = super.insert(model);
            model.setId(id);
        } else {
            model.setId(toCheck.getId());
            model.setCreatedAt(toCheck.getCreatedAt());
            super.update(model);
        }

        return model;
    }

    /**
     * Sets a partner row active flag to true.
     *
     * @param uuid  The row UUID
     * @return      True if row was updated, false if not
     * @throws      RepositoryUpdateException if an SQL error occurs
     */
    public boolean setActive(String uuid) throws RepositoryUpdateException {
        if (uuid == null) {
            return false;
        }

        try {
            return getDao().setActive(uuid) > 0;
        } catch (SQLiteException e) {
            throw new RepositoryUpdateException(e);
        }
    }

    /**
     * Sets a partner row active flag to false.
     *
     * @param uuid  The row UUID
     * @return      True if row was updated, false if not
     * @throws      RepositoryUpdateException if an SQL error occurs
     */
    public boolean setInactive(String uuid) throws RepositoryUpdateException {
        if (uuid == null) {
            return false;
        }

        try {
            return getDao().setInactive(uuid) > 0;
        } catch (SQLiteException e) {
            throw new RepositoryUpdateException(e);
        }
    }

    /**
     * Sets all partner rows active flag to false.
     *
     * @throws RepositoryUpdateException if an SQL error occurs
     */
    public void setAllInactive() throws RepositoryUpdateException {
        try {
            getDao().setAllInactive();
        } catch (SQLiteException e) {
            throw new RepositoryUpdateException(e);
        }
    }
}
