package com.oscarrrweb.sarva.data.mappers.sample;

import com.oscarrrweb.sarva.data.entity.sample.GizmoEntity;
import com.oscarrrweb.sarva.data.mappers.base.EntityMapper;
import com.oscarrrweb.sarva.domain.model.sample.Gizmo;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class GizmoMapper extends EntityMapper<GizmoEntity, Gizmo> {

    @Inject WidgetMapper mWidgetMapper;

    @Inject
    public GizmoMapper() {}

    @Override
    public GizmoEntity fromDomainModel(Gizmo domainModel) {
        if (domainModel == null) {
            return null;
        }

        GizmoEntity entity = new GizmoEntity();
        entity = (GizmoEntity) EntityMapper.setEntityFields(entity, domainModel);
        entity.setName(domainModel.getName());
        entity.setDescription(domainModel.getDescription());
        entity.setWidgets(mWidgetMapper.fromDomainModel(domainModel.getWidgets()));
        return entity;
    }

    @Override
    public List<GizmoEntity> fromDomainModel(List<Gizmo> domainModels) {
        if (domainModels == null) {
            return null;
        }

        List<GizmoEntity> entityList = new ArrayList<>();
        for (Gizmo model : domainModels) {
            entityList.add(fromDomainModel(model));
        }
        return entityList;
    }

    @Override
    public Gizmo toDomainModel(GizmoEntity entity) {
        if (entity == null) {
            return null;
        }

        Gizmo model = new Gizmo();
        model = (Gizmo) EntityMapper.setDomainModelFields(model, entity);
        model.setName(entity.getName());
        model.setDescription(entity.getDescription());
        model.setWidgets(mWidgetMapper.toDomainModel(entity.getWidgets()));
        return model;
    }

    @Override
    public List<Gizmo> toDomainModel(List<GizmoEntity> entities) {
        if (entities == null) {
            return null;
        }

        List<Gizmo> modelList = new ArrayList<>();
        for (GizmoEntity entity : entities) {
            modelList.add(toDomainModel(entity));
        }
        return modelList;
    }
}
