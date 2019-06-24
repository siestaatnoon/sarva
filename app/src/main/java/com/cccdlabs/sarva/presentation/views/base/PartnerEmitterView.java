package com.cccdlabs.sarva.presentation.views.base;

import com.cccdlabs.sarva.presentation.model.partners.PartnerUiModel;

import java.util.List;

public interface PartnerEmitterView extends BaseView {

    void showPartners(List<PartnerUiModel> partners);

    void onEmissionStarted();

    void onEmissionStopped();
}
