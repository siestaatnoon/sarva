package com.oscarrrweb.sarva.data.entity.sample;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.google.gson.annotations.SerializedName;

import static com.oscarrrweb.sarva.data.entity.sample.DoodadEntity.TABLE_NAME;

@Entity(tableName = TABLE_NAME,
        indices = {
                @Index(value = "uuid", unique = true),
                @Index("widget_uuid")
        },
        foreignKeys = @ForeignKey(
                entity = WidgetEntity.class,
                parentColumns = "uuid",
                childColumns = "widget_uuid"
        )
)
public class DoodadEntity extends com.oscarrrweb.sarva.data.entity.base.Entity {

    protected static final String TABLE_NAME = "doodads";

    @ColumnInfo(name = "widget_uuid")
    @SerializedName("widget_uuid")
    private String widgetUuid;

    private String name;

    private String description;

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public String getWidgetUuid() {
        return widgetUuid;
    }

    public void setWidgetUuid(String widgetUuid) {
        this.widgetUuid = widgetUuid;
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
}
