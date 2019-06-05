package com.cccdlabs.sarva.data.entity.sample;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import static com.cccdlabs.sarva.data.entity.sample.WidgetEntity.TABLE_NAME;

@Entity(tableName = TABLE_NAME,
        indices = {
                @Index(value = "uuid", unique = true),
                @Index("gizmo_uuid")
        },
        foreignKeys = {
                @ForeignKey(
                        entity = GizmoEntity.class,
                        parentColumns = "uuid",
                        childColumns = "gizmo_uuid"
                )
        }
)
public class WidgetEntity extends com.cccdlabs.sarva.data.entity.base.Entity {

    protected static final String TABLE_NAME = "widgets";

    @ColumnInfo(name = "gizmo_uuid")
    @SerializedName("gizmo_uuid")
    private String gizmoUuid;

    private String name;

    private String description;

    @Ignore
    private List<DoodadEntity> doodads;


    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public String getGizmoUuid() {
        return gizmoUuid;
    }

    public void setGizmoUuid(String gizmoUuid) {
        this.gizmoUuid = gizmoUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<DoodadEntity> getDoodads() {
        return doodads;
    }

    public void setDoodads(List<DoodadEntity> doodads) {
        this.doodads = doodads;
    }

}