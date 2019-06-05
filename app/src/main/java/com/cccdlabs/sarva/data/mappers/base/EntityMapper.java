package com.cccdlabs.sarva.data.mappers.base;

import com.cccdlabs.sarva.data.entity.base.Entity;
import com.cccdlabs.sarva.domain.model.base.Model;

import java.util.List;

/**
 * Abstraction to map an {@link Entity} subclass in the <code>data</code> package with an
 * {@link Model} subclass in the <code>domain</code> package. Converts single objects or
 * {@link List} of objects to and from the model objects of each package. Although the getter and
 * setter methods between the two packages may have similar names, keeping the models in their
 * respective packages eliminates coupling between them.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
abstract public class EntityMapper<E extends Entity, M extends Model>
        implements Mapper<E, M> {

    /**
     * Converts an {@link Model} subclass to an {@link Entity} subclass. To be defined.
     *
     * @param domainModel   The <code>domain</code> package model
     * @return              The converted <code>data</code> package Entity model
     */
    abstract public E fromDomainModel(M domainModel);

    /**
     * Converts a {@link List} of subclassed {@link Model} to a List of subclassed
     * {@link Entity}. To be defined.
     *
     * @param domainModels  The List of <code>domain</code> package models
     * @return              The converted List of <code>data</code> package Entity models
     */
    abstract public List<E> fromDomainModel(List<M> domainModels);

    /**
     * Converts an {@link Entity} subclass to an {@link Model} subclass. To be defined.
     *
     * @param entity   The <code>data</code> package Entity model
     * @return         The converted <code>domain</code> package model
     */
    abstract public M toDomainModel(E entity);

    /**
     * Converts a {@link List} of subclassed {@link Entity} to a List of subclassed
     * {@link Model}. To be defined.
     *
     * @param entities  The List of <code>data</code> package Entity models
     * @return          The converted List of <code>domain</code> package models
     */
    abstract public List<M> toDomainModel(List<E> entities);

    /**
     * Converts the base fields of a given {@link Model} subclass to the base fields
     * of a given {@link Entity} subclass. A convenience method that can be used by subclasses.
     *
     * @param entity        The <code>data</code> model to convert to
     * @param domainModel   The <code>domain</code> package model to convert
     * @return              The converted Entity
     */
    public static Entity setEntityFields(Entity entity, Model domainModel) {
        if (entity == null || domainModel == null) {
            return entity;
        }

        entity.setId(domainModel.getId());
        entity.setUuid(domainModel.getUuid());
        entity.setCreatedAt(domainModel.getCreatedAt());
        entity.setUpdatedAt(domainModel.getUpdatedAt());
        return entity;
    }

    /**
     * Converts the base fields of a given {@link Entity} subclass to the base fields of a given
     * {@link Model} subclass. A convenience method that can be used by subclass.
     *
     * @param domainModel   The <code>domain</code> model to convert to
     * @param entity        The <code>data</code> model to convert
     * @return              The converted AbstractModel
     */
    public static Model setDomainModelFields(Model domainModel, Entity entity) {
        if (domainModel == null || entity == null) {
            return domainModel;
        }

        domainModel.setId(entity.getId());
        domainModel.setUuid(entity.getUuid());
        domainModel.setCreatedAt(entity.getCreatedAt());
        domainModel.setUpdatedAt(entity.getUpdatedAt());
        return domainModel;
    }
}
