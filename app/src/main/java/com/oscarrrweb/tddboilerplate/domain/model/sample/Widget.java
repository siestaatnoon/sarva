package com.oscarrrweb.tddboilerplate.domain.model.sample;

import com.oscarrrweb.tddboilerplate.domain.model.base.AbstractModel;

import java.util.List;

public class Widget extends AbstractModel {

    public String gizmoUuid;

    public String name;

    public String value;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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