package com.cccdlabs.sarva.presentation.mappers.partners;

import com.cccdlabs.sarva.domain.model.partners.Partner;
import com.cccdlabs.sarva.presentation.mappers.base.UiModelMapper;
import com.cccdlabs.sarva.presentation.model.partners.PartnerUiModel;

import java.util.ArrayList;
import java.util.List;

public class PartnerUiModelMapper {

    public static PartnerUiModel fromDomainModel(Partner domainModel) {
        if (domainModel == null) {
            return null;
        }

        PartnerUiModel uiModel = new PartnerUiModel();
        uiModel = (PartnerUiModel) UiModelMapper.fromDomainModel(uiModel, domainModel);
        uiModel.setUsername(domainModel.getUsername());
        uiModel.setDeviceName(domainModel.getDeviceName());
        uiModel.setActive(domainModel.isActive());
        uiModel.setDistance(domainModel.getDistance());
        uiModel.setAccuracy(domainModel.getAccuracy());
        uiModel.setRssi(domainModel.getRssi());
        uiModel.setTxPower(domainModel.getTxPower());
        return uiModel;

    }

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

    public static Partner toDomainModel(PartnerUiModel uiModel) {
        if (uiModel == null) {
            return null;
        }

        Partner model = new Partner();
        model = (Partner) UiModelMapper.toDomainModel(model, uiModel);
        model.setUsername(uiModel.getUsername());
        model.setDeviceName(uiModel.getDeviceName());
        model.setActive(uiModel.isActive());
        model.setDistance(uiModel.getDistance());
        model.setAccuracy(uiModel.getAccuracy());
        model.setRssi(uiModel.getRssi());
        model.setTxPower(uiModel.getTxPower());
        return model;
    }

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
