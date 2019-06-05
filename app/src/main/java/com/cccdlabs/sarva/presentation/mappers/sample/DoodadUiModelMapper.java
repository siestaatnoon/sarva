package com.cccdlabs.sarva.presentation.mappers.sample;

import com.cccdlabs.sarva.domain.model.sample.Doodad;
import com.cccdlabs.sarva.presentation.mappers.base.UiModelMapper;
import com.cccdlabs.sarva.presentation.model.sample.DoodadUiModel;

import java.util.ArrayList;
import java.util.List;

public final class DoodadUiModelMapper {

    public static DoodadUiModel fromDomainModel(Doodad domainModel) {
        if (domainModel == null) {
            return null;
        }

        DoodadUiModel uiModel = new DoodadUiModel();
        uiModel = (DoodadUiModel) UiModelMapper.fromDomainModel(uiModel, domainModel);
        uiModel.setWidgetUuid(domainModel.getWidgetUuid());
        uiModel.setName(domainModel.getName());
        uiModel.setDescription(domainModel.getDescription());
        return uiModel;
    }

    public static List<DoodadUiModel> fromDomainModel(List<Doodad> domainModels) {
        if (domainModels == null) {
            return null;
        }

        List<DoodadUiModel> modelList = new ArrayList<>();
        for (Doodad model : domainModels) {
            modelList.add(fromDomainModel(model));
        }
        return modelList;
    }

    public static Doodad toDomainModel(DoodadUiModel uiModel) {
        if (uiModel == null) {
            return null;
        }

        Doodad model = new Doodad();
        model = (Doodad) UiModelMapper.toDomainModel(model, uiModel);
        model.setWidgetUuid(uiModel.getWidgetUuid());
        model.setName(uiModel.getName());
        model.setDescription(uiModel.getDescription());
        return model;
    }

    public static List<Doodad> toDomainModel(List<DoodadUiModel> uiModels) {
        if (uiModels == null) {
            return null;
        }

        List<Doodad> modelList = new ArrayList<>();
        for (DoodadUiModel uiModel : uiModels) {
            modelList.add(toDomainModel(uiModel));
        }
        return modelList;
    }
}
