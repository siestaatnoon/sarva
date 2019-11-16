package com.cccdlabs.sarva.presentation.views.partners;

import com.cccdlabs.sarva.presentation.model.partners.PartnerUiModel;
import com.cccdlabs.sarva.presentation.views.base.PartnerEmitterView;

import java.util.List;

public interface PartnerCheckView extends PartnerEmitterView {

    void showPartners(List<PartnerUiModel> models);

    void onClickPartnerAdd(PartnerUiModel uiModel);

    void onClickPartnerDelete(PartnerUiModel uiModel);

    void onPartnerSetActive(PartnerUiModel uiModel);

    void onPartnerAdded(PartnerUiModel uiModel);

    void onPartnerDeleted(PartnerUiModel uiModel, boolean hasDeleted);
}
