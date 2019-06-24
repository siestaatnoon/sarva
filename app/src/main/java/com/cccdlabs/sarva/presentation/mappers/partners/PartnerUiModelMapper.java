package com.cccdlabs.sarva.presentation.mappers.partners;

import com.cccdlabs.sarva.domain.model.partners.Partner;
import com.cccdlabs.sarva.presentation.mappers.base.UiModelMapper;
import com.cccdlabs.sarva.presentation.model.partners.PartnerUiModel;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link com.cccdlabs.sarva.presentation.mappers.base.Mapper} for converting to and from
 * {@link Partner} objects from the <code>domain</code> package and {@link PartnerUiModel} objects
 * in the <code>presentation</code> package.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class PartnerUiModelMapper {

    /**
     * Converts a {@link Partner} object to {@link PartnerUiModel} object. If null value passed
     * in, will return null.
     *
     * @param domainModel   The Partner object to convert
     * @return              The converted PartnerUiModel object
     */
    public static PartnerUiModel fromDomainModel(Partner domainModel) {
        if (domainModel == null) {
            return null;
        }

        PartnerUiModel uiModel = new PartnerUiModel();
        uiModel = (PartnerUiModel) UiModelMapper.fromDomainModel(uiModel, domainModel);
        uiModel.setUsername(domainModel.getUsername());
        uiModel.setDeviceName(domainModel.getDeviceName());
        uiModel.setActive(domainModel.isActive());
        uiModel.setEmitting(domainModel.isEmitting());
        uiModel.setDistance(domainModel.getDistance());
        uiModel.setAccuracy(domainModel.getAccuracy());
        uiModel.setRssi(domainModel.getRssi());
        uiModel.setTxPower(domainModel.getTxPower());
        return uiModel;

    }

    /**
     * Converts a {@link List} of {@link Partner} objects to a List of {@link PartnerUiModel}
     * objects. If null value passed in, will return null.
     *
     * @param domainModels  The List of Partner objects to convert
     * @return              The converted List of PartnerUiModel objects
     */
    public static List<PartnerUiModel> fromDomainModel(List<Partner> domainModels) {
        if (domainModels == null) {
            return null;
        }

        List<PartnerUiModel> modelList = new ArrayList<>();
        for (Partner model : domainModels) {
            modelList.add(fromDomainModel(model));
        }
        return modelList;
    }

    /**
     * Converts a {@link PartnerUiModel} object to {@link Partner} object. If null value passed
     * in, will return null.
     *
     * @param uiModel   The PartnerUiModel object to convert
     * @return          The converted Partner object
     */
    public static Partner toDomainModel(PartnerUiModel uiModel) {
        if (uiModel == null) {
            return null;
        }

        Partner model = new Partner();
        model = (Partner) UiModelMapper.toDomainModel(model, uiModel);
        model.setUsername(uiModel.getUsername());
        model.setDeviceName(uiModel.getDeviceName());
        model.setActive(uiModel.isActive());
        model.setEmitting(uiModel.isEmitting());
        model.setDistance(uiModel.getDistance());
        model.setAccuracy(uiModel.getAccuracy());
        model.setRssi(uiModel.getRssi());
        model.setTxPower(uiModel.getTxPower());
        return model;
    }

    /**
     * Converts a {@link List} of {@link PartnerUiModel} objects to a List of {@link Partner}
     * objects. If null value passed in, will return null.
     *
     * @param uiModels  The List of PartnerUiModel objects to convert
     * @return          The converted List of Partner objects
     */
    public static List<Partner> toDomainModel(List<PartnerUiModel> uiModels) {
        if (uiModels == null) {
            return null;
        }

        List<Partner> modelList = new ArrayList<>();
        for (PartnerUiModel entity : uiModels) {
            modelList.add(toDomainModel(entity));
        }
        return modelList;
    }
}
