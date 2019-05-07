package com.oscarrrweb.sarva.data.entity.sample;

import java.util.List;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import static com.oscarrrweb.sarva.data.entity.sample.GizmoEntity.TABLE_NAME;

@Entity(tableName = TABLE_NAME,
        indices = {
                @Index(value = "uuid", unique = true),
                @Index("created_at"),
                @Index("updated_at")
        }
)
public class GizmoEntity extends com.oscarrrweb.sarva.data.entity.base.Entity {

    protected static final String TABLE_NAME = "gizmos";

    private String name;

    private String description;

    @Ignore
    private List<WidgetEntity> widgets;

    @Override
    public String getTableName() {
        return TABLE_NAME;
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

    public List<WidgetEntity> getWidgets() {
        return widgets;
    }

    public void setWidgets(List<WidgetEntity> widgets) {
        this.widgets = widgets;
    }
}
