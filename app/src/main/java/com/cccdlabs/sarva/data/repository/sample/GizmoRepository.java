package com.cccdlabs.sarva.data.repository.sample;

import android.database.sqlite.SQLiteException;

import com.cccdlabs.sarva.data.entity.sample.GizmoEntity;
import com.cccdlabs.sarva.data.entity.sample.WidgetEntity;
import com.cccdlabs.sarva.data.mappers.sample.GizmoMapper;
import com.cccdlabs.sarva.data.mappers.sample.WidgetMapper;
import com.cccdlabs.sarva.data.repository.base.AbstractRepository;
import com.cccdlabs.sarva.data.storage.dao.sample.GizmoDao;
import com.cccdlabs.sarva.data.storage.dao.sample.WidgetDao;
import com.cccdlabs.sarva.domain.model.sample.Gizmo;
import com.cccdlabs.sarva.domain.model.sample.Widget;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryQueryException;
import com.cccdlabs.sarva.domain.repository.sample.GizmoRepo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class GizmoRepository extends AbstractRepository<GizmoEntity, Gizmo, GizmoMapper, GizmoDao> implements GizmoRepo {

    @Inject WidgetRepository mWidgetRepository;
    @Inject GizmoMapper mGizmoMapper;
    @Inject WidgetMapper mWidgetMapper;
    @Inject WidgetDao mWidgetDao;

    @Inject
    public GizmoRepository() {}

    @Override
    public Gizmo attachWidgets(Gizmo model) throws RepositoryQueryException {
        if (model == null) {
            return model;
        }

        // since we do not know or need to know fields of the domain
        // model, we need to convert the domain model to an entity
        GizmoEntity entity = mGizmoMapper.fromDomainModel(model);

        List<WidgetEntity> list;
        try{
            list = mWidgetDao.getByGizmo(entity.getUuid());
        } catch (SQLiteException e) {
            throw new RepositoryQueryException(e);
        }

        List<Widget> widgets = mWidgetMapper.toDomainModel(list);
        mWidgetRepository.attachDoodads(widgets);
        model.setWidgets(widgets);
        return model;
    }

    @Override
    public List<Gizmo> attachWidgets(List<Gizmo> models) throws RepositoryQueryException {
        if (models == null || models.size() == 0) {
            return models;
        }

        List<String> uuids = new ArrayList<>();
        for (Gizmo item : models) {
            // since we do not know or need to know fields of the domain
            // model, we need to convert the domain model to an entity
            GizmoEntity entity = mGizmoMapper.fromDomainModel(item);

            String uuid = entity.getUuid();
            if ( ! uuids.contains(uuid)) {
                uuids.add(uuid);
            }
        }

        List<WidgetEntity> items;
        try{
            items = mWidgetDao.getByGizmos(uuids);
        } catch (SQLiteException e) {
            throw new RepositoryQueryException(e);
        }

        List<Widget> widgets = mWidgetMapper.toDomainModel(items);
        widgets = mWidgetRepository.attachDoodads(widgets);

        for (Gizmo item : models) {
            // since we do not know or need to know fields of the domain
            // model, we need to convert the domain model to an entity
            GizmoEntity entity = mGizmoMapper.fromDomainModel(item);

            List<Widget> entities = new ArrayList<>();
            String currUuid = entity.getUuid();
            for (Widget widget : widgets) {
                // since we do not know or need to know fields of the domain
                // model, we need to convert the domain model to an entity
                WidgetEntity widgetEntity = mWidgetMapper.fromDomainModel(widget);

                if (currUuid.equals(widgetEntity.getGizmoUuid())) {
                    entities.add(widget);
                }
            }
            item.setWidgets(entities);
        }

        return models;
    }
}
