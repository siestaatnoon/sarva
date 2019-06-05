package com.cccdlabs.sarva.presentation.mappers.sample;

import com.cccdlabs.sarva.domain.model.sample.Gizmo;
import com.cccdlabs.sarva.presentation.mappers.base.UiModelMapper;
import com.cccdlabs.sarva.presentation.model.sample.GizmoUiModel;

import java.util.ArrayList;
import java.util.List;

public final class GizmoUiModelMapper {

    public static GizmoUiModel fromDomainModel(Gizmo domainModel) {
        if (domainModel == null) {
            return null;
        }

        GizmoUiModel uiModel = new GizmoUiModel();
        uiModel = (GizmoUiModel) UiModelMapper.fromDomainModel(uiModel, domainModel);
        uiModel.setName(domainModel.getName());
        uiModel.setDescription(domainModel.getDescription());
        uiModel.setWidgets(WidgetUiModelMapper.fromDomainModel(domainModel.getWidgets()));
        return uiModel;
    }

    public static List<GizmoUiModel> fromDomainModel(List<Gizmo> domainModels) {
        if (domainModels == null) {
            return null;
        }

        List<GizmoUiModel> modelList = new ArrayList<>();
        for (Gizmo model : domainModels) {
            modelList.add(fromDomainModel(model));
        }
        return modelList;
    }

    public static Gizmo toDomainModel(GizmoUiModel uiModel) {
        if (uiModel == null) {
            return null;
        }

        Gizmo model = new Gizmo();
        model = (Gizmo) UiModelMapper.toDomainModel(model, uiModel);
        model.setName(uiModel.getName());
        model.setDescription(uiModel.getDescription());
        model.setWidgets(WidgetUiModelMapper.toDomainModel(uiModel.getWidgets()));
        return model;
    }

    public static List<Gizmo> toDomainModel(List<GizmoUiModel> uiModels) {
        if (uiModels == null) {
            return null;
        }

        List<Gizmo> modelList = new ArrayList<>();
        for (GizmoUiModel uiModel : uiModels) {
            modelList.add(toDomainModel(uiModel));
        }
        return modelList;
    }
}
