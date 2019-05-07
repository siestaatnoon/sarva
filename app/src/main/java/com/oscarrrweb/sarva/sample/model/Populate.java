package com.oscarrrweb.sarva.sample.model;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonSyntaxException;
import com.oscarrrweb.sarva.data.entity.base.Entity;
import com.oscarrrweb.sarva.data.entity.sample.DoodadEntity;
import com.oscarrrweb.sarva.data.entity.sample.GizmoEntity;
import com.oscarrrweb.sarva.data.entity.sample.WidgetEntity;
import com.oscarrrweb.sarva.sample.data.DataSource;
import com.oscarrrweb.sarva.sample.utils.SeederUtils;

import java.util.ArrayList;
import java.util.List;

public class Populate {

    private static final String TAG = Populate.class.getSimpleName();
    private Context mContext;
    private DataSource mData;

    public Populate(Context context) {
        mContext = context;
        mData = new DataSource(mContext);
    }

    public boolean clear() {
        return mData.truncateTables() && mData.resetAutoIncrements();
    }

    public void close() {
        mData.close();
    }

    public List<String> gizmos() {
        ArrayList<String> results = new ArrayList<>();
        int resId = mContext.getResources().getIdentifier("seeder", "raw", mContext.getPackageName());
        String json = SeederUtils.getRawResourceString(mContext, resId);
        json = SeederUtils.getJsonArrayStringFromMember(json, "gizmos");
        if (json == null) {
            Log.e(TAG, "JSON resource [seeder.json] not found or missing member [gizmos]");
            return null;
        }

        try {
            List<Entity> entities = Entity.fromJsonArray(json, GizmoEntity.class);
            if (entities == null) {
                Log.e(TAG, "JSON resource [seeder.json] parse error occurred");
                return null;
            }

            for (Entity entity : entities) {
                GizmoEntity gizmo = (GizmoEntity) entity;
                int id = mData.insertGizmo(gizmo);
                String message = id == -1
                        ? "Gizmo: " + gizmo.getName() + " insert FAILED"
                        : "Gizmo ID " + id + ": " + gizmo.getName() + " added";
                results.add(message);
            }
        } catch (JsonSyntaxException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
        
        return results;
    }

    public List<String> widgets() {
        ArrayList<String> results = new ArrayList<>();
        int resId = mContext.getResources().getIdentifier("seeder", "raw", mContext.getPackageName());
        String json = SeederUtils.getRawResourceString(mContext, resId);
        json = SeederUtils.getJsonArrayStringFromMember(json, "widgets");
        if (json == null) {
            Log.e(TAG, "JSON resource [seeder.json] not found or missing member [widgets]");
            return null;
        }

        try {
            List<Entity> entities = Entity.fromJsonArray(json, WidgetEntity.class);
            if (entities == null) {
                Log.e(TAG, "JSON resource [seeder.json] parse error occurred");
                return null;
            }

            for (Entity entity : entities) {
                WidgetEntity widget = (WidgetEntity) entity;
                int id = mData.insertWidget(widget);
                String message = id == -1
                        ? "Widget: " + widget.getName() + " insert FAILED"
                        : "Widget ID " + id + ": " + widget.getName() + " added";
                results.add(message);
            }
        } catch (JsonSyntaxException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }

        return results;
    }

    public List<String> doodads() {
        ArrayList<String> results = new ArrayList<>();
        int resId = mContext.getResources().getIdentifier("seeder", "raw", mContext.getPackageName());
        String json = SeederUtils.getRawResourceString(mContext, resId);
        json = SeederUtils.getJsonArrayStringFromMember(json, "doodads");
        if (json == null) {
            Log.e(TAG, "JSON resource [seeder.json] not found or missing member [doodads]");
            return null;
        }

        try {
            List<Entity> entities = Entity.fromJsonArray(json, DoodadEntity.class);
            if (entities == null) {
                Log.e(TAG, "JSON resource [seeder.json] parse error occurred");
                return null;
            }

            for (Entity entity : entities) {
                DoodadEntity doodad = (DoodadEntity) entity;
                int id = mData.insertDoodad(doodad);
                String message = id == -1
                        ? "Doodad: " + doodad.getName() + " insert FAILED"
                        : "Doodad ID " + id + ": " + doodad.getName() + " added";
                results.add(message);
            }
        } catch (JsonSyntaxException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }

        return results;
    }
}
