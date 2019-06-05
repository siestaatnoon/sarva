package com.cccdlabs.sarva.data.repository.sample;

import android.database.sqlite.SQLiteException;

import com.cccdlabs.sarva.data.entity.sample.DoodadEntity;
import com.cccdlabs.sarva.data.entity.sample.GizmoEntity;
import com.cccdlabs.sarva.data.entity.sample.WidgetEntity;
import com.cccdlabs.sarva.data.mappers.sample.DoodadMapper;
import com.cccdlabs.sarva.data.mappers.sample.GizmoMapper;
import com.cccdlabs.sarva.data.mappers.sample.WidgetMapper;
import com.cccdlabs.sarva.data.repository.base.AbstractRepository;
import com.cccdlabs.sarva.data.storage.dao.sample.DoodadDao;
import com.cccdlabs.sarva.data.storage.dao.sample.GizmoDao;
import com.cccdlabs.sarva.data.storage.dao.sample.WidgetDao;
import com.cccdlabs.sarva.domain.model.sample.Widget;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryDeleteException;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryQueryException;
import com.cccdlabs.sarva.domain.repository.sample.WidgetRepo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

public class WidgetRepository extends AbstractRepository<WidgetEntity, Widget, WidgetMapper, WidgetDao> implements WidgetRepo {

    @Inject WidgetMapper mWidgetMapper;
    @Inject DoodadMapper mDoodadMapper;
    @Inject GizmoMapper mGizmoMapper;
    @Inject WidgetDao mWidgetDao;
    @Inject GizmoDao mGizmoDao;
    @Inject DoodadDao mDoodadDao;

    @Inject
    public WidgetRepository() {}

    @Override
    public int delete(String uuid) throws RepositoryDeleteException {
        WidgetEntity model = mWidgetDao.fromUuid(uuid);
        if (model == null) {
            return 0;
        }

        try {
            // first delete doodads attached to widget
            List<DoodadEntity> list = mDoodadDao.getByWidget(model.getUuid());
            if (list != null) {
                Object[] objects = list.toArray();
                DoodadEntity[] doodad = Arrays.copyOf(objects, objects.length, DoodadEntity[].class);
                mDoodadDao.delete(doodad);
            }

            return mWidgetDao.delete(model);
        } catch (SQLiteException e) {
            // This is most likely due to a foreign key existing
            // in another table for the row to delete
            throw new RepositoryDeleteException(e);
        }
    }

    @Override
    public int delete(Widget model) throws RepositoryDeleteException {
        WidgetEntity entity = mWidgetMapper.fromDomainModel(model);
        if (entity != null) {
            return this.delete(entity.getUuid());
        }

        return 0;
    }

    @Override
    public int delete(int id) throws RepositoryDeleteException {
        WidgetEntity entity = mWidgetDao.fromId(id);
        if (entity != null) {
            return this.delete(entity.getUuid());
        }

        return 0;
    }

    @Override
    public Widget attachGizmo(Widget model) throws RepositoryQueryException {
        if (model == null) {
            return model;
        }

        // since we do not know or need to know fields of the domain
        // model, we need to convert the domain model to an entity
        WidgetEntity entity = mWidgetMapper.fromDomainModel(model);

        try{
            GizmoEntity gizmo = mGizmoDao.fromUuid(entity.getGizmoUuid());
            model.setGizmo(mGizmoMapper.toDomainModel(gizmo));
        } catch (SQLiteException e) {
            // This is most likely due to a foreign key existing
            // in another table for the row to delete
            throw new RepositoryQueryException(e);
        }
        return model;
    }

    @Override
    public List<Widget> attachGizmo(List<Widget> models) throws RepositoryQueryException {
        if (models == null || models.size() == 0) {
            return models;
        }

        List<String> uuids = new ArrayList<>();
        for (Widget item : models) {
            // since we do not know or need to know fields of the domain
            // model, we need to convert the domain model to an entity
            WidgetEntity entity = mWidgetMapper.fromDomainModel(item);

            String uuid = entity.getGizmoUuid();
            if ( ! uuids.contains(uuid)) {
                uuids.add(uuid);
            }
        }

        List<GizmoEntity> gizmos;
        try{
            gizmos = mGizmoDao.fromUuids(uuids);
        } catch (SQLiteException e) {
            throw new RepositoryQueryException(e);
        }

        for (Widget item : models) {
            // since we do not know or need to know fields of the domain
            // model, we need to convert the domain model to an entity
            WidgetEntity entity = mWidgetMapper.fromDomainModel(item);

            String uuid = entity.getGizmoUuid();
            for (GizmoEntity gizmo : gizmos) {
                if (uuid.equals(gizmo.getUuid())) {
                    item.setGizmo(mGizmoMapper.toDomainModel(gizmo));
                    break;
                }
            }
        }

        return models;
    }

    @Override
    public Widget attachDoodads(Widget model) throws RepositoryQueryException {
        if (model == null) {
            return model;
        }

        // since we do not know or need to know fields of the domain
        // model, we need to convert the domain model to an entity
        WidgetEntity entity = mWidgetMapper.fromDomainModel(model);

        List<DoodadEntity> list;
        try{
            list = mDoodadDao.getByWidget(entity.getUuid());
        } catch (SQLiteException e) {
            throw new RepositoryQueryException(e);
        }

        model.setDoodads(mDoodadMapper.toDomainModel(list));
        return model;
    }

    @Override
    public List<Widget> attachDoodads(List<Widget> models) throws RepositoryQueryException {
        if (models == null || models.size() == 0) {
            return models;
        }

        List<String> uuids = new ArrayList<>();
        for (Widget item : models) {
            // since we do not know or need to know fields of the domain
            // model, we need to convert the domain model to an entity
            WidgetEntity entity = mWidgetMapper.fromDomainModel(item);

            String uuid = entity.getUuid();
            if ( ! uuids.contains(uuid)) {
                uuids.add(uuid);
            }
        }

        List<DoodadEntity> doodads;
        try{
            doodads = mDoodadDao.getByWidgets(uuids);
        } catch (SQLiteException e) {
            throw new RepositoryQueryException(e);
        }

        for (Widget item : models) {
            // since we do not know or need to know fields of the domain
            // model, we need to convert the domain model to an entity
            WidgetEntity entity = mWidgetMapper.fromDomainModel(item);

            List<DoodadEntity> entities = new ArrayList<>();
            String currUuid = entity.getUuid();
            for (DoodadEntity doodad : doodads) {
                if (currUuid.equals(doodad.getWidgetUuid())) {
                    entities.add(doodad);
                }
            }
            item.setDoodads(mDoodadMapper.toDomainModel(entities));
        }

        return models;
    }
}
