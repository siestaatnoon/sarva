package com.cccdlabs.sarva.data.repository.base;

import android.database.sqlite.SQLiteException;

import com.cccdlabs.sarva.data.entity.base.Entity;
import com.cccdlabs.sarva.data.mappers.base.Mapper;
import com.cccdlabs.sarva.data.storage.dao.base.EntityDao;
import com.cccdlabs.sarva.domain.model.base.Model;
import com.cccdlabs.sarva.domain.repository.base.Repository;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryDeleteException;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryInsertException;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryQueryException;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryUpdateException;

import java.lang.reflect.Array;
import java.util.List;

import javax.inject.Inject;

/**
 * Abstraction for straightforward database INSERT, UPDATE, DELETE and SELECT (by ID OR UUID)
 * via POJO objects. Methods can be overridden to account for complex queries or relations.
 * Note that this class handles mapping between {@link Entity} and {@link Model} objects.
 *
 * @param <E>   {@link Entity} subclass type from <code>data</code> package
 * @param <T>   {@link Model} subclass type from <code>domain</code> package
 * @param <M>   {@link Mapper} subclass type that maps the data between the <code><E></code>
 *              and <code><T></code> object types
 * @param <D>   {@link EntityDao} subclass type for <code><T></code> entity type
 */
abstract public class AbstractRepository<E extends Entity, T extends Model,
        M extends Mapper<E, T>, D extends EntityDao<E>> implements Repository<T> {

    /**
     * {@link EntityDao} database access object. {@link Inject} annotated to enable
     * Dagger dependency injection of this object.
     */
    @Inject D mDao;

    /**
     * {@link Mapper} to convert POJO objects between the <code>data</code> and <code>domain</code>
     * packages. {@link Inject} annotated to enable Dagger dependency injection of this object.
     */
    @Inject M mMapper;

    /**
     * Constructor.
     */
    protected AbstractRepository() {}

    /**
     * Performs a database INSERT given the fields of a  {@link Model} subclass POJO object.
     * Note that this method ensures a valid UUID and created/updated timestamps are set before
     * performing the query.
     *
     * @param model The POJO object containing the fields (member variables) and corresponding
     *              model values to insert
     * @return int  The AUTO_INCREMENT id of the inserted row
     * @throws      RepositoryInsertException if model parameter null or an SQL error occurs in the
     *              INSERT query
     */
    @Override
    public int insert(T model) throws RepositoryInsertException {
        if (model == null) {
            throw new RepositoryInsertException("Parameter <T> model is null value");
        }

        E entity = mMapper.fromDomainModel(model);

        // make sure uuid is set
        entity.setUuid();

        // set created/updated timestamps
        if (entity.getCreatedAt() == null || entity.getUpdatedAt() == null) {
            entity.touch();
        }

        try {
            return (int) mDao.insert(entity);
        } catch (SQLiteException e) {
            // This is most likely due to entity having a foreign
            // key to a row that no longer exists
            throw new RepositoryInsertException(e);
        }
    }

    /**
     * Performs a database UPDATE given the fields of a  {@link Model} subclass POJO object.
     * Note that this method updates the update timestamp to current date/time and ensures
     * the original AUTO_INCREMENT id and created timestamp are not changed.
     * <p>
     * NOTE: If an {@link SQLiteException} occurs, it will be logged and -1 will be returned.
     *
     * @param model The POJO object containing the fields (member variables) and corresponding
     *              model values to update
     * @return int  The number of rows successfully updated
     * @throws      RepositoryUpdateException if model parameter null or an SQL error occurs in the
     *              UPDATE query
     */
    @Override
    public int update(T model) throws RepositoryUpdateException {
        if (model == null) {
            throw new RepositoryUpdateException("Parameter <T> model is null value");
        }

        E updated = mMapper.fromDomainModel(model);

        // set updated timestamp
        updated.touch();

        E original = mDao.fromUuid(updated.getUuid());
        if (original == null) {
            // original model was deleted or doesn't exist
            throw new RepositoryUpdateException("Model [uuid: " + updated.getUuid() + "] does not exist");
        }

        // make sure ID and created at date hasn't changed or set it if from synced entity
        updated.setId(original.getId());
        updated.setCreatedAt(original.getCreatedAt());

        try {
            return mDao.update(updated);
        } catch (SQLiteException e) {
            // This is most likely due to entity having a foreign
            // key to a row that no longer exists
            throw new RepositoryUpdateException(e);
        }
    }

    /**
     * Performs a database DELETE given a {@link Model} subclass POJO object. If the model does
     * not exist, 0 will be returned.
     *
     * @param model The POJO object to perform delete
     * @return int  The number of rows successfully deleted
     * @throws      RepositoryDeleteException if model parameter null or an SQL error occurs
     *              in the DELETE query
     */
    @Override
    public int delete(T model) throws RepositoryDeleteException {
        if (model == null) {
            throw new RepositoryDeleteException("Parameter <T> model is null value");
        }

        int affectedRows = 0;
        try {
            E entity = mMapper.fromDomainModel(model);
            if (entity != null) {
                return delete(entity.getUuid());
            }
        } catch (SQLiteException e) {
            // This is most likely due to a foreign key existing
            // in another table for the row to delete
            throw new RepositoryDeleteException(e);
        }

        return affectedRows;
    }

    /**
     * Performs a database DELETE given a row UUID. If the model does not exist, 0 will be
     * returned.
     *
     * @param uuid  The UUID of row to perform delete
     * @return int  The number of rows successfully deleted
     * @throws      RepositoryDeleteException if UUID parameter null or empty or an SQL error occurs
     *              in the DELETE query
     */
    @Override
    public int delete(String uuid) throws RepositoryDeleteException {
        if (uuid == null || uuid.equals("")) {
            throw new RepositoryDeleteException("Parameter uuid is null or empty value");
        }

        int affectedRows = 0;

        try {
            E model = mDao.fromUuid(uuid);
            if (model != null) {
                affectedRows = mDao.delete(model);
            }
        } catch (SQLiteException e) {
            // This is most likely due to a foreign key existing
            // in another table for the row to delete
            throw new RepositoryDeleteException(e);
        }

        return affectedRows;
    }

    /**
     * Performs a database DELETE given a row AUTO_INCREMENT id. If the model does not exist,
     * 0 will be returned.
     *
     * @param id    The id of row to perform delete
     * @return int  The number of rows successfully deleted
     * @throws      RepositoryDeleteException if an SQL error occurs in the DELETE query
     */
    @Override
    public int delete(int id) throws RepositoryDeleteException {
        try {
            E entity = mDao.fromId(id);
            if (entity != null) {
                return delete(entity.getUuid());
            }
        } catch (SQLiteException e) {
            // This is most likely due to a foreign key existing
            // in another table for the row to delete
            throw new RepositoryDeleteException(e);
        }

        return 0;
    }

    /**
     * Performs a database DELETE given a {@link List} {@link Model} subclass POJO object.
     * If no models are deleted, 0 will be returned.
     * <p>
     * NOTE: The models are deleted base on the AUTO_INCREMENT id set in the models. If
     * not set in the models, they will not be deleted.
     *
     * @param models    The {@link List} of POJO objects containing the ID and/or UUID of the model
     *                  to delete
     * @return          The number of rows successfully deleted, may be zero if model(s) do
     *                  not exist
     * @throws          RepositoryDeleteException if models parameter null or an error occurs while
     *                  deleting the model(s)
     */
    @SuppressWarnings("unchecked")
    @Override
    public int delete(List<T> models) throws RepositoryDeleteException {
        if (models == null) {
            throw new RepositoryDeleteException("Parameter List<T> models is null value");
        } else if (models.size() == 0) {
            return 0;
        }


        List<E> entityList = mMapper.fromDomainModel(models);

        // convert list to array, a bit hacky but should work for generics
        Class clazz = null;
        for (E entity : entityList) {
            if (entity != null) {
                clazz = entity.getClass();
                break;
            }
        }
        if (clazz == null) {
            throw new RepositoryDeleteException("Parameter List<T> models contains null value(s)");
        }
        E[] entities = entityList.toArray((E[]) Array.newInstance(clazz, entityList.size()));

        try {
            return mDao.delete(entities);
        } catch (SQLiteException e) {
            // This is most likely due to a foreign key existing
            // in another table for the row to delete
            throw new RepositoryDeleteException(e);
        }
    }

    /**
     * Returns all rows of a table as a {@link List} of <code><T></code> type.
     *
     * @return  List<T> All table rows as POJO objects
     * @throws  RepositoryQueryException if an SQL error occurs in the SELECT query
     */
    @Override
    public List<T> getAll() throws RepositoryQueryException {
        try {
            List<E> entities = mDao.getAll();
            return mMapper.toDomainModel(entities);
        } catch (SQLiteException e) {
            throw new RepositoryQueryException(e);
        }
    }

    /**
     * Returns a single row from a given AUTO_INCREMENT id.
     *
     * @param   id  The id of row to retrieve
     * @return  T   The row as POJO object
     * @throws      RepositoryQueryException if an SQL error occurs in the SELECT query
     */
    @Override
    public T getById(int id) throws RepositoryQueryException {
        try {
            E entity = mDao.fromId(id);
            return mMapper.toDomainModel(entity);
        } catch (SQLiteException e) {
            throw new RepositoryQueryException(e);
        }
    }

    /**
     * Returns a single row from a given UUID.
     *
     * @param uuid  The UUID of row to retrieve
     * @return T    The row as POJO object
     * @throws      RepositoryQueryException if UUID parameter null or empty value or an SQL error
     *              occurs in the SELECT query
     */
    @Override
    public T getByUuid(String uuid) throws RepositoryQueryException {
        if (uuid == null || uuid.equals("")) {
            throw new RepositoryQueryException("Parameter uuid is null or empty value");
        }

        try {
            E entity = mDao.fromUuid(uuid);
            return mMapper.toDomainModel(entity);
        } catch (SQLiteException e) {
            throw new RepositoryQueryException(e);
        }
    }

    /**
     * Returns the Room database DAO.
     *
     * @return The database DAO
     */
    protected D getDao() {
        return mDao;
    }

    /**
     * Returns the domain/entity mapper.
     *
     * @return The domain/entity mapper
     */
    protected M getMapper() {
        return mMapper;
    }
}
