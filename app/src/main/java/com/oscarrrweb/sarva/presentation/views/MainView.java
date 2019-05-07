package com.oscarrrweb.sarva.presentation.views;

import com.oscarrrweb.sarva.presentation.model.sample.GizmoUiModel;
import com.oscarrrweb.sarva.presentation.views.base.BaseView;

import java.util.List;

public interface MainView extends BaseView {
    void showGizmos(List<GizmoUiModel> gizmos);
}
