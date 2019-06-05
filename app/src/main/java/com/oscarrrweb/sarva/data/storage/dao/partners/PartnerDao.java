package com.oscarrrweb.sarva.data.storage.dao.partners;

import com.oscarrrweb.sarva.data.entity.partners.PartnerEntity;
import com.oscarrrweb.sarva.data.storage.dao.base.EntityDao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;

/**
 * Abstraction for {@link PartnerEntity} functions using a {@link androidx.room.Room}
 * database. Annotated with {@link Dao} for Room data access object designation.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
@Dao
abstract public class PartnerDao implements EntityDao<PartnerEntity> {

    /**
     * Performs a DELETE by UUID.
     *
     * @param uuid  The UUID
     * @return      The number of rows deleted
     */
    @Query("DELETE FROM partners WHERE uuid=:uuid")
    abstract public int delete(String uuid);

    /**
     * Performs a SELECT retrieving a row by AUTO_INCREMENT id.
     *
     * @param id    The AUTO_INCREMENT id
     * @return      The row as a PartnerEntity object
     */
    @Query("SELECT * FROM partners WHERE id=:id LIMIT 1")
    abstract public PartnerEntity fromId(int id);

    /**
     * Performs a SELECT retrieving a row by UUID.
     *
     * @param uuid  The UUID
     * @return      The row as a PartnerEntity object
     */
    @Query("SELECT * FROM partners WHERE uuid=:uuid LIMIT 1")
    abstract public PartnerEntity fromUuid(String uuid);

    /**
     * Performs a SELECT retrieving all rows.
     *
     * @return The {@link List} of PartnerEntity objects
     */
    @Query("SELECT * FROM partners ORDER BY is_active DESC, username ASC")
    abstract public List<PartnerEntity> getAll();

    /**
     * Performs a SELECT retrieving all rows that are active.
     *
     * @return The {@link List} of PartnerEntity objects that are active
     */
    @Query("SELECT * FROM partners WHERE is_active=1 ORDER BY username ASC")
    abstract public List<PartnerEntity> getActive();

    /**
     * Performs a SELECT retrieving all rows that are inactive.
     *
     * @return The {@link List} of PartnerEntity objects that are inactive
     */
    @Query("SELECT * FROM partners WHERE is_active=0 ORDER BY username ASC")
    abstract public List<PartnerEntity> getInactive();

    /**
     * Performs an UPDATE setting the active flag to true.
     *
     * @param uuid  The UUID
     * @return      The number of rows updated
     */
    @Query("UPDATE partners SET is_active=1 WHERE uuid=:uuid")
    abstract public int setActive(String uuid);

    /**
     * Performs an UPDATE setting the active flag to false.
     *
     * @param uuid  The UUID
     * @return      The number of rows updated
     */
    @Query("UPDATE partners SET is_active=0 WHERE uuid=:uuid")
    abstract public int setInactive(String uuid);

    /**
     * Performs an UPDATE setting the active flag to false for all rows.
     *
     * @return The number of rows updated
     */
    @Query("UPDATE partners SET is_active=0")
    abstract public int setAllInactive();

    /**
     * Performs an UPDATE the update timestamp of a row.
     *
     * @param uuid      The UUID of the row
     * @param dateTime  The SQL timestamp
     * @return          The number of rows updated
     */
    @Query("UPDATE partners SET updated_at=:dateTime WHERE uuid=:uuid")
    abstract public int setUpdated(String uuid, String dateTime);
}
