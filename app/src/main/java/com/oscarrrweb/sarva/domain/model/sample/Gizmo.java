package com.oscarrrweb.sarva.domain.model.sample;

import com.oscarrrweb.sarva.domain.model.base.Model;

import java.util.List;

public class Gizmo extends Model {

    private String name;

    private String description;

    private List<Widget> widgets;


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

    public List<Widget> getWidgets() {
        return widgets;
    }

    public void setWidgets(List<Widget> widgets) {
        this.widgets = widgets;
    }
}
