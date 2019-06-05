package com.oscarrrweb.sarva.data.mappers.partners;

import com.oscarrrweb.sarva.data.entity.partners.PartnerEntity;
import com.oscarrrweb.sarva.data.mappers.base.EntityMapper;
import com.oscarrrweb.sarva.domain.model.partners.Partner;

import java.util.ArrayList;
import java.util.List;

public class PartnerMapper extends EntityMapper<PartnerEntity, Partner> {

    @Override
    public PartnerEntity fromDomainModel(Partner domainModel) {
        if (domainModel == null) {
            return null;
        }

        PartnerEntity entity = new PartnerEntity();
        entity = (PartnerEntity) EntityMapper.setEntityFields(entity, domainModel);
        entity.setUsername(domainModel.getUsername());
        entity.setDeviceName(domainModel.getDeviceName());
        entity.setActive(domainModel.isActive());
        entity.setDistance(domainModel.getDistance());
        entity.setAccuracy(domainModel.getAccuracy());
        entity.setRssi(domainModel.getRssi());
        entity.setTxPower(domainModel.getTxPower());
        return entity;

    }

    @Override
    public List<PartnerEntity> fromDomainModel(List<Partner> domainModels) {
        if (domainModels == null) {
            return null;
        }

        List<PartnerEntity> entityList = new ArrayList<>();
        for (Partner model : domainModels) {
            entityList.add(fromDomainModel(model));
        }
        return entityList;
    }

    @Override
    public Partner toDomainModel(PartnerEntity entity) {
        if (entity == null) {
            return null;
        }

        Partner model = new Partner();
        model = (Partner) EntityMapper.setDomainModelFields(model, entity);
        model.setUsername(entity.getUsername());
        model.setDeviceName(entity.getDeviceName());
        model.setActive(entity.isActive());
        model.setDistance(entity.getDistance());
        model.setAccuracy(entity.getAccuracy());
        model.setRssi(entity.getRssi());
        model.setTxPower(entity.getTxPower());
        return model;
    }

    @Override
    public List<Partner> toDomainModel(List<PartnerEntity> entities) {
        if (entities == null) {
            return null;
        }

        List<Partner> modelList = new ArrayList<>();
        for (PartnerEntity entity : entities) {
            modelList.add(toDomainModel(entity));
        }
        return modelList;
    }
}
