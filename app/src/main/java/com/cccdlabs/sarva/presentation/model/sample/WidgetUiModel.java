package com.cccdlabs.sarva.presentation.model.sample;

import com.cccdlabs.sarva.presentation.model.base.UiModel;

import java.util.List;

public class WidgetUiModel extends UiModel {

    public String gizmoUuid;

    public String name;

    public String description;

    public GizmoUiModel gizmo;

    public List<DoodadUiModel> doodads;


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

    public GizmoUiModel getGizmo() {
        return gizmo;
    }

    public void setGizmo(GizmoUiModel gizmo) {
        this.gizmo = gizmo;
    }

    public List<DoodadUiModel> getDoodads() {
        return doodads;
    }

    public void setDoodads(List<DoodadUiModel> doodads) {
        this.doodads = doodads;
    }
}
