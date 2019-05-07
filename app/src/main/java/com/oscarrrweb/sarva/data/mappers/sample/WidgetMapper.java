package com.oscarrrweb.sarva.data.mappers.sample;

import com.oscarrrweb.sarva.data.entity.sample.WidgetEntity;
import com.oscarrrweb.sarva.data.mappers.base.EntityMapper;
import com.oscarrrweb.sarva.domain.model.sample.Widget;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class WidgetMapper extends EntityMapper<WidgetEntity, Widget> {

    @Inject DoodadMapper mDoodadMapper;

    @Inject
    public WidgetMapper() {}

    @Override
    public WidgetEntity fromDomainModel(Widget domainModel) {
        if (domainModel == null) {
            return null;
        }

        WidgetEntity entity = new WidgetEntity();
        entity = (WidgetEntity) EntityMapper.setEntityFields(entity, domainModel);
        entity.setGizmoUuid(domainModel.getGizmoUuid());
        entity.setName(domainModel.getName());
        entity.setDescription(domainModel.getDescription());
        entity.setDoodads(mDoodadMapper.fromDomainModel(domainModel.getDoodads()));
        return entity;
    }

    @Override
    public List<WidgetEntity> fromDomainModel(List<Widget> domainModels) {
        if (domainModels == null) {
            return null;
        }

        List<WidgetEntity> entityList = new ArrayList<>();
        for (Widget model : domainModels) {
            entityList.add(fromDomainModel(model));
        }
        return entityList;
    }

    @Override
    public Widget toDomainModel(WidgetEntity entity) {
        if (entity == null) {
            return null;
        }

        Widget model = new Widget();
        model = (Widget) EntityMapper.setDomainModelFields(model, entity);
        model.setGizmoUuid(entity.getGizmoUuid());
        model.setName(entity.getName());
        model.setDescription(entity.getDescription());
        model.setDoodads(mDoodadMapper.toDomainModel(entity.getDoodads()));
        return model;
    }

    @Override
    public List<Widget> toDomainModel(List<WidgetEntity> entities) {
        if (entities == null) {
            return null;
        }

        List<Widget> modelList = new ArrayList<>();
        for (WidgetEntity entity : entities) {
            modelList.add(toDomainModel(entity));
        }
        return modelList;
    }
}
