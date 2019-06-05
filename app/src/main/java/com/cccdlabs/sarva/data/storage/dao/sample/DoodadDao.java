package com.cccdlabs.sarva.data.storage.dao.sample;

import androidx.room.Dao;
import androidx.room.Query;

import com.cccdlabs.sarva.data.entity.sample.DoodadEntity;
import com.cccdlabs.sarva.data.storage.dao.base.EntityDao;

import java.util.List;

@Dao
abstract public class DoodadDao implements EntityDao<DoodadEntity> {

    @Query("DELETE FROM doodads WHERE uuid=:uuid")
    abstract public int delete(String uuid);

    @Query("SELECT * FROM doodads WHERE id=:id LIMIT 1")
    abstract public DoodadEntity fromId(int id);

    @Query("SELECT * FROM doodads WHERE uuid=:uuid LIMIT 1")
    abstract public DoodadEntity fromUuid(String uuid);

    @Query("SELECT * FROM doodads ORDER BY widget_uuid ASC, id ASC")
    abstract public List<DoodadEntity> getAll();

    @Query("SELECT * FROM doodads WHERE widget_uuid=:widgetUuid ORDER BY id ASC")
    abstract public List<DoodadEntity> getByWidget(String widgetUuid);

    @Query("SELECT * FROM doodads WHERE widget_uuid IN (:widgetUuids) ORDER BY widget_uuid ASC, id ASC")
    abstract public List<DoodadEntity> getByWidgets(List<String> widgetUuids);

    @Query("UPDATE doodads SET updated_at=:dateTime WHERE uuid=:uuid")
    abstract public int setUpdatedAt(String uuid, String dateTime);
}
