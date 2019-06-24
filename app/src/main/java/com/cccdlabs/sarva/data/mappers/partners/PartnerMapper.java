package com.cccdlabs.sarva.data.mappers.partners;

import com.cccdlabs.sarva.data.entity.partners.PartnerEntity;
import com.cccdlabs.sarva.data.mappers.base.EntityMapper;
import com.cccdlabs.sarva.domain.model.partners.Partner;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Converts {@link Partner} to and from {@link PartnerEntity} model objects. Converts single
 * objects or {@link List} of objects to and from the model objects of each package.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class PartnerMapper extends EntityMapper<PartnerEntity, Partner> {

    /**
     * Constructor. Annotated with {@link Inject} for use with Dagger 2 dependency injection.
     */
    @Inject
    public PartnerMapper() {}

    /**
     * Converts an {@link Partner} subclass to an {@link PartnerEntity} subclass.
     *
     * @param domainModel   The Partner model
     * @return              The converted PartnerEntity model
     */
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
        entity.setEmitting(domainModel.isEmitting());
        entity.setDistance(domainModel.getDistance());
        entity.setAccuracy(domainModel.getAccuracy());
        entity.setRssi(domainModel.getRssi());
        entity.setTxPower(domainModel.getTxPower());
        return entity;

    }

    /**
     * Converts a {@link List} of subclassed {@link Partner} to a List of subclassed
     * {@link PartnerEntity}.
     *
     * @param domainModels  The List of Partner models
     * @return              The converted List of PartnerEntity models
     */
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

    /**
     * Converts an {@link PartnerEntity} subclass to an {@link Partner} subclass.
     *
     * @param entity   The PartnerEntity model
     * @return         The converted Partner model
     */
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
        model.setEmitting(entity.isEmitting());
        model.setDistance(entity.getDistance());
        model.setAccuracy(entity.getAccuracy());
        model.setRssi(entity.getRssi());
        model.setTxPower(entity.getTxPower());
        return model;
    }

    /**
     * Converts a {@link List} of subclassed {@link PartnerEntity} to a List of subclassed
     * {@link Partner}.
     *
     * @param entities  The List of PartnerEntity models
     * @return          The converted List of Partner package models
     */
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
