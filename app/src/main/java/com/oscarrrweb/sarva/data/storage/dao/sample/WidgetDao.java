package com.oscarrrweb.sarva.data.storage.dao.sample;

import com.oscarrrweb.sarva.data.entity.sample.WidgetEntity;
import com.oscarrrweb.sarva.data.storage.dao.base.EntityDao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
abstract public class WidgetDao implements EntityDao<WidgetEntity> {

    @Query("DELETE FROM widgets WHERE uuid=:uuid")
    abstract public int delete(String uuid);

    @Query("SELECT * FROM widgets WHERE id=:id LIMIT 1")
    abstract public WidgetEntity fromId(int id);

    @Query("SELECT * FROM widgets WHERE uuid=:uuid LIMIT 1")
    abstract public WidgetEntity fromUuid(String uuid);

    @Query("SELECT * FROM widgets WHERE gizmo_uuid=:gizmoUuid ORDER BY id ASC")
    abstract public List<WidgetEntity> getByGizmo(String gizmoUuid);

    @Query("SELECT * FROM widgets WHERE gizmo_uuid IN (:gizmoUuids) ORDER BY gizmo_uuid ASC, id ASC")
    abstract public List<WidgetEntity> getByGizmos(List<String> gizmoUuids);

    @Query("SELECT * FROM widgets ORDER BY id ASC")
    abstract public List<WidgetEntity> getAll();

    @Query("UPDATE widgets SET updated_at=:dateTime WHERE uuid=:uuid")
    abstract public int setUpdatedAt(String uuid, String dateTime);
}

