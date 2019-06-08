package com.cccdlabs.sarva.presentation.mappers.base;

import com.cccdlabs.sarva.domain.model.base.Model;
import com.cccdlabs.sarva.presentation.model.base.UiModel;

import java.util.List;

/**
 * Abstraction to map an {@link UiModel} subclass in the <code>presentation</code> package with a
 * {@link Model} subclass in the <code>domain</code> package. Converts single objects or
 * {@link List} of objects to and from the model objects of each package. Although the getter and
 * setter methods between the two packages may have similar names, keeping the models in their
 * respective packages eliminates coupling between them.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public interface Mapper<U extends UiModel, M extends Model> {

    /**
     * Converts an {@link Model} subclass to an {@link UiModel} subclass.
     *
     * @param domainModel   The <code>domain</code> package model
     * @return              The converted <code>data</code> package UiModel model
     */
    U fromDomainModel(M domainModel);

    /**
     * Converts a {@link List} of subclassed {@link Model} to a List of subclassed
     * {@link UiModel}.
     *
     * @param domainModels  The List of <code>domain</code> package models
     * @return              The converted List of <code>data</code> package UiModel models
     */
    List<U> fromDomainModel(List<M> domainModels);

    /**
     * Converts an {@link UiModel} subclass to an {@link Model} subclass.
     *
     * @param uiModel  The <code>data</code> package UiModel model
     * @return         The converted <code>domain</code> package model
     */
    M toDomainModel(U uiModel);

    /**
     * Converts a {@link List} of subclassed {@link UiModel} to a List of subclassed
     * {@link Model}.
     *
     * @param uiModels  The List of <code>data</code> package UiModel models
     * @return          The converted List of <code>domain</code> package models
     */
    List<M> toDomainModel(List<U> uiModels);
}
