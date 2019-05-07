package com.oscarrrweb.sarva.data.storage.dao.base;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

import java.util.List;

/**
 * Abstraction for {@link androidx.room.Room} database data access object methods. Annotated
 * with {@link Dao} to signify this.
 *
 * @author Johnny Spence
 * @version 1.0.0
 * @param <T>   The Room database entity class, typically a POJO object
 */
@Dao
public interface EntityDao<T> {

    /**
     * Performs an INSERT from an entity object. If the object has the same id as an existing
     * row, will replace the row with the new values from the entity.
     *
     * @param entity    The entity object
     * @return          The AUTO_INCREMENT id after inserting
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(T entity);

    /**
     * Performs an INSERT from an array of entity objects. If the object(s) has the same id as an
     * existing row, will replace the row with the new values from the entity.
     *
     * @param entities  The array of entity objects
     * @return          The array of corresponding AUTO_INCREMENT ids after inserting
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insert(T[] entities);

    /**
     * Performs an UPDATE from an entity object.
     *
     * @param entity    The entity object
     * @return          The number of rows updated or zero if no values changed for the row
     */
    @Update
    int update(T entity);

    /**
     * Performs a DELETE from an entity object. The id value must be set and correspond to a
     * row to be deleted.
     *
     * @param entity    The entity object
     * @return          The number of rows deleted or zero if row not found
     */
    @Delete
    int delete(T entity);

    /**
     * Performs a DELETE from an array of entity objects. The id value must be set for each entity
     * and correspond to a row to be deleted.
     *
     * @param entities  The array of entity objects
     * @return          The number of rows deleted
     */
    @Delete
    int delete(T[] entities);

    /**
     * Performs a SELECT and retrieves a single row by AUTO_INCREMENT id value.
     *
     * @param id    The AUTO_INCREMENT id
     * @return      The entity object
     */
    T fromId(int id);

    /**
     * Performs a SELECT and retrieves a single row by UUID value. Note that, along with the
     * AUTO_INCREMENT id, the UUID is unique to the table for use as an identifier
     * across platforms.
     *
     * @param uuid  The UUID
     * @return      The entity object
     */
    T fromUuid(String uuid);

    /**
     * Retrieves all rows of a table. Useful for small tables, not recommended for large ones.
     *
     * @return The {@link List} of all entity objects in a table
     */
    List<T> getAll();
}
