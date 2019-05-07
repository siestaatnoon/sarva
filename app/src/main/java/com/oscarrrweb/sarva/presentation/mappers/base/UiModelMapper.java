package com.oscarrrweb.sarva.presentation.mappers.base;

import com.oscarrrweb.sarva.domain.model.base.Model;
import com.oscarrrweb.sarva.presentation.model.base.UiModel;

/**
 * Maps the base fields of a <code>data</code> package model to the base fields of a
 * <code>presentation</code> package model.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public final class UiModelMapper {

    /**
     * Converts the base fields of a given {@link Model} subclass to the base fields
     * of a given {@link UiModel} subclass.
     *
     * @param uiModel       The <code>presentation</code> model to convert to
     * @param domainModel   The <code>domain</code> package model to convert
     * @return              The converted model
     */
    public static UiModel fromDomainModel(UiModel uiModel, Model domainModel) {
        if (uiModel == null || domainModel == null) {
            return uiModel;
        }

        uiModel.setId(domainModel.getId());
        uiModel.setUuid(domainModel.getUuid());
        uiModel.setCreatedAt(domainModel.getCreatedAt());
        uiModel.setUpdatedAt(domainModel.getUpdatedAt());
        return uiModel;
    }

    /**
     * Converts the base fields of a given {@link UiModel} subclass to the base fields of a given
     * {@link Model} subclass.
     *
     * @param domainModel   The <code>domain</code> model to convert to
     * @param uiModel       The <code>presentation</code> model to convert
     * @return              The converted model
     */
    public static Model toDomainModel(Model domainModel, UiModel uiModel) {
        if (domainModel == null || uiModel == null) {
            return domainModel;
        }

        domainModel.setId(uiModel.getId());
        domainModel.setUuid(uiModel.getUuid());
        domainModel.setCreatedAt(uiModel.getCreatedAt());
        domainModel.setUpdatedAt(uiModel.getUpdatedAt());
        return domainModel;
    }
}
