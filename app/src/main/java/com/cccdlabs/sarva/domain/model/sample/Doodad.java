package com.cccdlabs.sarva.domain.model.sample;

import com.cccdlabs.sarva.domain.model.base.Model;

public class Doodad extends Model {

    private String widgetUuid;

    private String name;

    private String description;


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
