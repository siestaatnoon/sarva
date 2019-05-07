package com.oscarrrweb.sarva.presentation.mappers.sample;

import com.oscarrrweb.sarva.domain.model.sample.Widget;
import com.oscarrrweb.sarva.presentation.mappers.base.UiModelMapper;
import com.oscarrrweb.sarva.presentation.model.sample.WidgetUiModel;

import java.util.ArrayList;
import java.util.List;

public final class WidgetUiModelMapper {

    public static WidgetUiModel fromDomainModel(Widget domainModel) {
        if (domainModel == null) {
            return null;
        }

        WidgetUiModel uiModel = new WidgetUiModel();
        uiModel = (WidgetUiModel) UiModelMapper.fromDomainModel(uiModel, domainModel);
        uiModel.setGizmoUuid(domainModel.getGizmoUuid());
        uiModel.setName(domainModel.getName());
        uiModel.setDescription(domainModel.getDescription());
        uiModel.setDoodads(DoodadUiModelMapper.fromDomainModel(domainModel.getDoodads()));
        return uiModel;
    }

    public static List<WidgetUiModel> fromDomainModel(List<Widget> domainModels) {
        if (domainModels == null) {
            return null;
        }

        List<WidgetUiModel> modelList = new ArrayList<>();
        for (Widget model : domainModels) {
            modelList.add(fromDomainModel(model));
        }
        return modelList;
    }

    public static Widget toDomainModel(WidgetUiModel uiModel) {
        if (uiModel == null) {
            return null;
        }

        Widget model = new Widget();
        model = (Widget) UiModelMapper.toDomainModel(model, uiModel);
        model.setGizmoUuid(uiModel.getGizmoUuid());
        model.setName(uiModel.getName());
        model.setDescription(uiModel.getDescription());
        model.setDoodads(DoodadUiModelMapper.toDomainModel(uiModel.getDoodads()));
        return model;
    }

    public static List<Widget> toDomainModel(List<WidgetUiModel> uiModels) {
        if (uiModels == null) {
            return null;
        }

        List<Widget> modelList = new ArrayList<>();
        for (WidgetUiModel uiModel : uiModels) {
            modelList.add(toDomainModel(uiModel));
        }
        return modelList;
    }
}
