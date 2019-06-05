package com.cccdlabs.sarva.presentation.views;

import com.cccdlabs.sarva.presentation.model.sample.GizmoUiModel;
import com.cccdlabs.sarva.presentation.views.base.BaseView;

import java.util.List;

public interface MainView extends BaseView {
    void showGizmos(List<GizmoUiModel> gizmos);
}
