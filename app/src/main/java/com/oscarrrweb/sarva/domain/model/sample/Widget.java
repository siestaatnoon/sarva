package com.oscarrrweb.sarva.domain.model.sample;

import com.oscarrrweb.sarva.domain.model.base.Model;

import java.util.List;

public class Widget extends Model {

    public String gizmoUuid;

    public String name;

    public String description;

    public Gizmo gizmo;

    public List<Doodad> doodads;


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

    public Gizmo getGizmo() {
        return gizmo;
    }

    public void setGizmo(Gizmo gizmo) {
        this.gizmo = gizmo;
    }

    public List<Doodad> getDoodads() {
        return doodads;
    }

    public void setDoodads(List<Doodad> doodads) {
        this.doodads = doodads;
    }
}
