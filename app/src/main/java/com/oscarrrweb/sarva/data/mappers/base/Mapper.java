package com.oscarrrweb.sarva.data.mappers.base;

import com.oscarrrweb.sarva.data.entity.base.Entity;
import com.oscarrrweb.sarva.domain.model.base.Model;

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
public interface Mapper<E extends Entity, M extends Model> {

    /**
     * Converts an {@link Model} subclass to an {@link Entity} subclass.
     *
     * @param domainModel   The <code>domain</code> package model
     * @return              The converted <code>data</code> package Entity model
     */
    E fromDomainModel(M domainModel);

    /**
     * Converts a {@link List} of subclassed {@link Model} to a List of subclassed
     * {@link Entity}.
     *
     * @param domainModels  The List of <code>domain</code> package models
     * @return              The converted List of <code>data</code> package Entity models
     */
    List<E> fromDomainModel(List<M> domainModels);

    /**
     * Converts an {@link Entity} subclass to an {@link Model} subclass.
     *
     * @param entity   The <code>data</code> package Entity model
     * @return         The converted <code>domain</code> package model
     */
    M toDomainModel(E entity);

    /**
     * Converts a {@link List} of subclassed {@link Entity} to a List of subclassed
     * {@link Model}.
     *
     * @param entities  The List of <code>data</code> package Entity models
     * @return          The converted List of <code>domain</code> package models
     */
    List<M> toDomainModel(List<E> entities);
}
